import socket as socket
import time as time
import threading as threading
import struct




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
            #wait client to connect
            clientSock, addr = self.socket.accept()
            # while True:
            #     try:
            #         print clientSock.recv(64)
            #     except Exception, e:
            #         print e.message

            print "connection from: ", addr
            self.socks.append(clientSock)
            transThread = TcpServerTransThread(clientSock)
            self.tcpTransThreads.append(transThread)
            transThread.startTransThread()

        for thrd in self.tcpTransThreads:
            thrd.interrupt()

IDENTITY = ""

UPLOAD_FILE_REQUEST = "UPLOADFILE"
DOWNLOAD_FILE_REQUEST = "DOWNLOADFILE"

CANCEL_UPLOAD_REQUEST = "CANCELUPLOAD"

ASK_FILE_NAME_ANSWER = "ASK_FILE_NAME"
RECEIVE_FILE_ANSWER = "RECEIVE_FILE"

DEFAULT_PACKET_SIZE = 1024

# only created in Build Thread
class TcpServerTransThread(object):
    def __init__(self, clientSock):
        self.clientSock = clientSock
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

    def startTransThread(self):
        self.thread = threading.Thread(target=self.transThreadRun, args=())
        self.thread.start()
        return


    def handleIdentity(self):
        return

    def handleDownloadRequest(self):
        return

    def handleUploadRequest(self):
        try:
            self.clientSock.send(ASK_FILE_NAME_ANSWER)
            tmp = self.clientSock.recv(128)
            if tmp == CANCEL_UPLOAD_REQUEST:
                print "upload canceled"
                return 3

            prefix, filename = tmp.split(' ', 1)

            if prefix != "filename":
                return 2
            print "filename: ", filename
            self.clientSock.send(RECEIVE_FILE_ANSWER)
            print "start receiving file"
            file = open("./TestReceiveFile/"+filename, 'wb')

            header = self.clientSock.recv(4)
            fileSize = struct.unpack('i', header)[0]
            print "filesize: ", fileSize


            packetSize = DEFAULT_PACKET_SIZE
            packetCount = fileSize/packetSize
            lastSize = fileSize - packetCount * packetSize

            print packetCount, packetSize, lastSize


            # receive and write file
            t = fileSize
            while t > 0:
                packet = self.clientSock.recv(packetSize)
                l = len(packet)
                if t >= l:
                    file.write(packet)
                    t -= l
                else:
                    file.write(packet[0:t])
                    t = 0
                # print t


            print "finish uploading file!"
            file.close()


        except Exception, e:
            print "upload failed: ", e.message
            return 1
        return 0



    def transThreadRun(self):
        # TBD: disconnct after 30s if no data coming in
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

            except Exception, e:
                print "trans thread error, network environment might have changed", e.message
                break

            if not data:
                continue

            print data
