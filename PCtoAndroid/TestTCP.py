import socket

s = socket.socket()
host = socket.gethostname()
port = 8001
s.connect(("10.7.194.42", port))
print "Connected!"

while True:
    # dataReceive = s.recv(64)
    dataSend = input("Send message: ")
    s.send(dataSend.encode('utf-8'))
    # print dataReceive.decode()
