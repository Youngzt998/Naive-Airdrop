import socket as socket
import time as time
import threading as threading


class UdpBroadcastSendingThread(object):
    def __init__(self, myTcpPort, targetUdpPort=8003):
        # get my name and ip
        self.myName = socket.gethostname()
        self.myIP = socket.gethostbyname(self.myName)
        self.myTcpPort = myTcpPort
        self.myTcpPortLock = threading.Lock()

        # set udp mode
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        self.broadcastPort = targetUdpPort
        self.broadCastIP = "<broadcast>"


        self.thread = None
        self.exitState = False
        self.exitLock = threading.Lock()

    def startThread(self):
        self.thread = threading.Thread(target=self.run, args=())
        self.thread.start()
        return

    def changePort(self, newTcpPort):
        self.myTcpPort = newTcpPort

    def interrupt(self):
        self.exitLock.acquire()
        self.exitState = True
        self.exitLock.release()
        # self.thread.join()
        print "broad cast thread end!"
        return

    def isInterrupted(self):
        self.exitLock.acquire()
        result = (self.exitState == True)
        self.exitLock.release()
        return result

    def run(self):
        print "start broad cast"
        while not self.isInterrupted():
            # print "broad cast a message"
            try:
                self.myName = socket.gethostname()
                self.myIP = socket.gethostbyname(self.myName)
                self.socket.sendto(self.myName + " " + self.myIP + " " + str(self.myTcpPort), (self.broadCastIP, self.broadcastPort))
                time.sleep(2)
            except Exception, e:
                print "broadcast failed: ", e.message
                time.sleep(5)
