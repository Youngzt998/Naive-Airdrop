#
# # udp broadcast receiving test
# import socket
# import time
#
#
#
# hostname = socket.gethostname()
# ip = socket.gethostbyname(hostname)
# print hostname, ip
#
# # set udp mode
# s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
#
# broadcastPort = 8003
# broadCastIP = "<broadcast>"
#
# print "start broad cast"
# while True:
#     s.sendto(hostname+" "+ip, (broadCastIP, broadcastPort))
#     time.sleep(5)


# udp broadcast sending test

import ctypes
import socket

targetPort = 8004
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

s.bind(('', targetPort))
print "Listen to UDP on " + str(targetPort)
while True:
    data, addr = s.recvfrom(1024)
    print (addr, data)


# s = socket.socket()
# host = socket.gethostname()
# port = 8001
# s.connect(("10.7.194.42", port))
# print "Connected!"

# while True:
#     # dataReceive = s.recv(64)
#     dataSend = input("Send message: ")
#     s.send(dataSend.encode('utf-8'))
#     # print dataReceive.decode()
