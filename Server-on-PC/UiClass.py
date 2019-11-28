import Tkinter as tkinter

class FileDirGetUI(object):
    def __init__(self, tk):
        self.filePath = "C:\Users\young\Documents\Project\ComputerNetworkProject\PCtoAndroid\Files"

        self.fileDirHintLabel = tkinter.Label(tk, text="Select directory")
        self.fileDirHintLabel.pack(side=tkinter.LEFT)

        self.fileDirEntry = tkinter.Entry(tk, bd=5, width = 100)
        self.fileDirEntry.pack(side=tkinter.LEFT)
        self.fileDirEntry.insert(0, self.filePath)

        self.button = tkinter.Button(tk, text="conform", command=self.clickCallBack)
        self.button.pack(side=tkinter.LEFT)

    def clickCallBack(self):
        self.filePath = self.fileDirEntry.get()
        print self.filePath
        return

    def getPath(self):
        return self.filePath