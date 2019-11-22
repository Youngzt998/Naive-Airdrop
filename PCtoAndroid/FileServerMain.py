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

# keeping broad cast my name, ip. port once a few second
udpThread = UdpBroadCastThread.UdpBroadcastSendingThread(myTcpPort=myTcpPort, targetUdpPort=targetUdpPort)
udpThread.startThread()

tcpServerThread = TcpServerThread.TcpServerBuildThread(myTcpPort)
tcpServerThread.startMainThread()


top = Tkinter.Tk()
fileDirGetUI = UiClass.FileDirGetUI(top)
top.mainloop()




