import requests
import json
import xlwt
import random
from faker import Faker
from faker.providers import address

fake = Faker()
fake.add_provider(address)

outputfile = "crawl {}.xls".format(2)
query = "doan gioi"
token = "AIzaSyBA8gH7N9zLJs181hH80QM567IXqBQILSE"
gbapi = "https://www.googleapis.com/books/v1/volumes?q=" + query + "?key=" + token

print(gbapi)
data = requests.get(gbapi).json()

if 

workbook = xlwt.Workbook(encoding="utf-8")
sheet1 = workbook.add_sheet("Sheet 1")

def fileinit():
    sheet1.write(0, 0, "bookName")
    sheet1.write(0, 1, "price")
    sheet1.write(0, 2, "catId")
    sheet1.write(0, 3, "author")
    sheet1.write(0, 4, "pubId")
    sheet1.write(0, 5, "pubYear")
    sheet1.write(0, 6, "languageId")
    sheet1.write(0, 7, "location")
    sheet1.write(0, 8, "quantity")
    sheet1.write(0, 9, "availQuantity")

def printRecordToXLS(i, bookName, price, catId, author, pubId, pubYear, languageId, location, quantity, availQuantity):
    sheet1.write(i, 0, bookName)
    sheet1.write(i, 1, price)
    sheet1.write(i, 2, catId)
    sheet1.write(i, 3, author)
    sheet1.write(i, 4, pubId)
    sheet1.write(i, 5, pubYear)
    sheet1.write(i, 6, languageId)
    sheet1.write(i, 7, location)
    sheet1.write(i, 8, quantity)
    sheet1.write(i, 9, availQuantity)


fileinit()

i = 0
for item in data["items"]:
    i +=1
    bookName = item["volumeInfo"]["title"]
    price = 0
    try:
        catId = item["volumeInfo"]["categories"][0]
    except:
        catId = ""
    try:
        author = item["volumeInfo"]["authors"][0]
    except:
        author = ""
    pubId = 0
    pubYear =  item["volumeInfo"]["publishedDate"]
    languageId = item["saleInfo"]["country"]
    location = fake.military_apo()
    quantity = random.randint(3, 20)
    availQuantity = quantity

    printRecordToXLS(i, bookName, price, catId, author, pubId, pubYear, languageId, location, quantity, availQuantity)

workbook.save(outputfile)
