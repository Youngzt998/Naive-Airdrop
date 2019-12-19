import Tkinter as tkinter
import random
import string

import tkMessageBox
import qrcode
import matplotlib.pyplot as plt
import matplotlib.image as mpimg




import CipherClass




class TopUI(object):
    def __init__(self):
        self.top = tkinter.Tk()


class FileDirGetUI(object):
    def __init__(self, rootTK):
        file = open("./filepath.config", "r")
        self.filePath = file.read()
        file.close()

        self.fileDirHintLabel = tkinter.Label(rootTK, text="Select directory")
        self.fileDirHintLabel.pack(side=tkinter.LEFT)

        self.fileDirEntry = tkinter.Entry(rootTK, bd=5, width = 100)
        self.fileDirEntry.pack(side=tkinter.LEFT)
        self.fileDirEntry.insert(0, self.filePath)

        self.button = tkinter.Button(rootTK, text="conform", command=self.clickCallBack)
        self.button.pack(side=tkinter.LEFT)

    def clickCallBack(self):
        filePath = self.fileDirEntry.get()
        file = open("./filepath.config", 'wb')
        file.write(filePath)
        file.close()
        return

    def getPath(self):
        return self.filePath

# register a client, generate it's cipher, distribute is by QR code
class InitSetUI(object):
    def __init__(self, tk):
        self.button = tkinter.Button(tk, text="Register your devices:\n "
                                              "Click here and scan the instant QR Code\n"
                                              "Max number of devices is 3 ",
                                     command = self.clickCallBack)
        self.button.pack(side=tkinter.LEFT)



    def clickCallBack(self):
        # print "test"
        # print random
        clientId = ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(16)).encode('utf-8')
        serverId = ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(16)).encode('utf-8')
        key = ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(16)).encode('utf-8')
        iv = ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(16)).encode('utf-8')
        print "client id: ", clientId
        print "key: ", key


        file = open("./register.config", "a")
        file.write(clientId + ' ' + serverId + ' ' + key + ' ' + iv + '\n')
        file.close()

        cipher = CipherClass.CipherClass(id, key, iv)
        # print cipher.encrypt("test cipher")
        # print cipher.decrypt( cipher.encrypt("test cipher") )

        # definitely not a good way to show, but don't know a better one
        img = qrcode.make(clientId + ' ' + serverId + ' ' + key + ' ' + iv)
        img.show()

        # img.save('test.png')
        #
        # instantTK = tkinter.Tk()
        # instantTextLabel = tkinter.Label(instantTK, text = "Scan it to register", justify = tkinter.CENTER)
        # instantTextLabel.pack(side = tkinter.TOP)
        # instantImg = tkinter.PhotoImage(file = './test.gif')
        # instantImgLable = tkinter.Label(instantTK, image = instantImg)
        # instantImgLable.pack(side = tkinter.BOTTOM)
        # instantTK.mainloop()

        return