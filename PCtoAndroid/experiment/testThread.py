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
