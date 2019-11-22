import socket as socket
import time as time
import threading as threading

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


class TcpServerTransThread(object):
    def __init__(self, clientSock):
        print "ok1"
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

    def transThreadRun(self):
        # TBD: disconnct after 30s if no data coming in
        time = None
        print "start a tcp trans thread"
        while not self.isInterrupted():
            try:
                data = self.clientSock.recv(64)
                if data == b"":
                    print "client has disconnected socket"
                    break
                print data
            except Exception, e:
                print "trans thread error, network environment might have changed", e.message
                break

            if not data:
                continue

            print data
