import UdpBroadCastThread
import TcpServerThread
import UiClass

import Tkinter as Tkinter
import tkMessageBox
import qrcode
import matplotlib.pyplot as plt
import matplotlib.image as mpimg


# img = qrcode.make('hello, qrcode')
# img.save('test.png')

myTcpPort = 8002
targetUdpPort = 8003

open("./register.config", "a").close()
open("./filepath.config", "a").close()

# keeping broad cast my name, ip. port once a few second
udpThread = UdpBroadCastThread.UdpBroadcastSendingThread(myTcpPort=myTcpPort, targetUdpPort=targetUdpPort)
udpThread.startThread()

# build the server socket and provide service for mutiple client
tcpServerThread = TcpServerThread.TcpServerBuildThread(myTcpPort)
tcpServerThread.startMainThread()

# create UI
top = Tkinter.Tk()

initUI = UiClass.InitSetUI(top)
fileDirGetUI = UiClass.FileDirGetUI(top)

top.mainloop()
