import string
from Crypto.Cipher import AES
from binascii import b2a_hex, a2b_hex


# Using AES ECB
class CipherClass(object):
    def __init__(self, id, key):
        self.id = id
        self.key = key

    def add_to_16(self, text):
        t = (16 - len( text.encode('utf-8') ) % 16) % 16
        text = text + ('\0' * t)
        return text.encode('utf-8')

    def encrypt(self, text):
        return b2a_hex(  AES.new(self.key, AES.MODE_ECB).encrypt(self.add_to_16(text))  )

    def decrypt(self, text):
        return bytes.decode( AES.new(self.key, AES.MODE_ECB).decrypt(a2b_hex(text)) ).rstrip('\0')