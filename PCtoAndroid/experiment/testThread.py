import ctypes
import socket


s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

s.bind(('', 8004))
print 'Bind UDP on 8004...'
while True:
    data, addr = s.recvfrom(1024)
    print (addr, data)
