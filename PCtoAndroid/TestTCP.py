import socket

s = socket.socket()
host = socket.gethostname()
port = 8001
s.connect(("10.7.194.42", port))
while True:
    data = s.recv(64)
    print data.decode()
