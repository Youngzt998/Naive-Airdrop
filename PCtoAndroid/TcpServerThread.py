import socket as socket
import time as time
import threading as threading
import struct
import os



# firstly build a server, listen to file transfer request
class TcpServerBuildThread(object):
    def __init__(self, myTcpPort=8002):
        self.maxClientNum = 3

        self.host = ''
        self.port = myTcpPort
        self.addr = (self.host, self.port)

        # establish tcp socket
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(self.addr)
        self.socket.listen(self.maxClientNum)

        # list of clients sock
        self.socks = []
        self.tcpTransThreads = []

        self.exitState = False
        self.exitLock = threading.Lock()
        self.thread = None

    def interrupt(self):
        self.exitLock.acquire()
        self.exitState = True
        self.exitLock.release()
        # self.thread.join()
        print "server thread end!"
        return

    def isInterrupted(self):
        self.exitLock.acquire()
        result = (self.exitState == True)
        self.exitLock.release()
        return result

    def startMainThread(self):
        self.thread = threading.Thread(target=self.mainThreadRun, args=())
        self.thread.start()
        return

    def mainThreadRun(self):
        print "start tcp main thread"
        while not self.isInterrupted():
            # wait for a client to connect
            clientSock, addr = self.socket.accept()
            # while True:
            #     try:
            #         print clientSock.recv(64)
            #     except Exception, e:
            #         print e.message

            print "connection from: ", addr
            self.socks.append(clientSock)

            # start a subthread to listen provide service
            transThread = TcpServerTransThread(clientSock)
            self.tcpTransThreads.append(transThread)
            transThread.startTransThread()

        for thrd in self.tcpTransThreads:
            thrd.interrupt()

IDENTITY = ""

UPLOAD_FILE_REQUEST = "UPLOADFILE"
DOWNLOAD_FILE_REQUEST = "DOWNLOADFILE"

CANCEL_UPLOAD_REQUEST = "CANCELUPLOAD"

# cancel any activity
CANCEL_ALL = "CANCELALL"

ASK_FILE_NAME_ANSWER = "ASK_FILE_NAME"
FILE_EXISTS_ANSWER = "FILE_EXISTS"
RECEIVE_FILE_ANSWER = "RECEIVE_FILE"

DEFAULT_PACKET_SIZE = 1024



# only created in Build Thread
class TcpServerTransThread(object):
    def __init__(self, clientSock):
        self.clientSock = clientSock
        self.exitState = False
        self.exitLock = threading.Lock()
        self.thread = None
        self.syncDir = "./TestReceiveFile"


    def interrupt(self):
        self.exitLock.acquire()
        self.exitState = True
        self.exitLock.release()
        # self.thread.join()
        print "server thread end!"
        return

    def isInterrupted(self):
        self.exitLock.acquire()
        result = (self.exitState == True)
        self.exitLock.release()
        return result

    def startTransThread(self):
        self.thread = threading.Thread(target=self.transThreadRun, args=())
        self.thread.start()
        return



    def handleIdentityRequest(self):
        return

    def handleFileListRequest(self):
        return

    def handleDownloadRequest(self):
        return

    def handleUploadRequest(self):
        try:
            # ask the client to give the file name
            self.clientSock.send(ASK_FILE_NAME_ANSWER)

            # receive the file's name
            tmp = self.clientSock.recv(128)

            # some accident happens to the client
            if tmp == CANCEL_UPLOAD_REQUEST:
                print "upload canceled"
                return 3

            # the file's name; prefix was used for verification
            prefix, filenameWithPath = tmp.split(' ', 1)
            if prefix != "filename":
                return 2
            print "filename: ", filenameWithPath

            # handle filename
            dir = self.syncDir
            _, filename = filenameWithPath.split("/", 1)
            list = filename.split('/')
            filename = list.pop()
            for name in list:
                dir = dir + '/' + name
                if not os.path.exists(dir):
                    os.makedirs(dir)

            filePath = self.syncDir + filenameWithPath
            if os.path.exists(filePath):
                self.clientSock.send(FILE_EXISTS_ANSWER)
                return -1

            # ask the client to upload the whole file
            self.clientSock.send(RECEIVE_FILE_ANSWER)
            print "start receiving file"

            # open or create a file and write into it
            file = open(self.syncDir + filenameWithPath, 'wb')

            # header tells how many bytes the file's size was
            header = self.clientSock.recv(4)
            fileSize = struct.unpack('i', header)[0]
            print "filesize: ", fileSize

            # receive packet by packet
            packetSize = DEFAULT_PACKET_SIZE    # default: 1024

            # receive and write file
            t = fileSize
            while t > 0:
                if t >= packetSize:
                    # we don't know how many bytes it can receive one time
                    packet = self.clientSock.recv(packetSize)
                    l = len(packet)
                    t -= l
                    file.write(packet)
                else:
                    packet = self.clientSock.recv(t)
                    l = len(packet)
                    t -= l
                    file.write(packet)

            file.close()


        except Exception, e:
            print "upload failed: ", e.message
            return 1
        return 0



    def transThreadRun(self):
        # TBD: disconnect after 30s if no data coming in
        time = None
        print "start a tcp trans thread"
        while not self.isInterrupted():
            try:
                data = self.clientSock.recv(32)
                if data == b"":
                    print "client has disconnected socket"
                    break

                # print "request is: ", data
                if data == UPLOAD_FILE_REQUEST:
                    status = self.handleUploadRequest()
                    if status == 0:
                        print "upload file sucess!"
                    # elif status == -1:
                    #     print "upload file success!"

            except Exception, e:
                print "trans thread error, network environment might have changed", e.message
                break

            if not data:
                continue
