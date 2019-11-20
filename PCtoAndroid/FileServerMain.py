import UdpBroadCastThread
import Tkinter





top = Tkinter.Tk()
top.mainloop()


udpThread = UdpBroadCastThread.UdpBroadcastSendingThread(8002)
udpThread.startThread()
input("input anything to interrupt the thread")
udpThread.interrupt()
