import socket as socket
import time as time
import threading as threading


class UdpBroadcastSendingThread(object):
    def __init__(self, myTcpPort):
        # get my name and ip
        self.myName = socket.gethostname()
        self.myIP = socket.gethostbyname(self.myName)
        # set udp mode
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        self.broadcastPort = 8003
        self.broadCastIP = "<broadcast>"
        self.myTcpPort = myTcpPort

        self.thread = None
        self.exitState = False
        self.exitLock = threading.Lock()

    def startThread(self):
        self.thread = threading.Thread(target=self.run, args=())
        self.thread.start()
        return


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
            self.socket.sendto(self.myName + " " + self.myIP + " " + str(self.myTcpPort), (self.broadCastIP, self.broadcastPort))
            time.sleep(2)

