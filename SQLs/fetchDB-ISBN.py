import requests
import json
import xlwt
import random
from faker import Faker
from faker.providers import address
from faker.providers import lorem


fake = Faker()
fake.add_provider(address)
fake.add_provider(lorem)

def book_query(query, number):
    outputfile = "crawl {}.xls".format(number)
    h = {'Authorization': '43559_b65ae51d92ac7f7e8fe3828677302816'}
    api = "https://api2.isbndb.com/books/" + query + "?page=1&pageSize=1000&beta=0"

    resp = requests.get(api, headers=h)

    if (resp.status_code != 200):
        print(resp)
    else:
        data = resp.json()

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
        for item in data["books"]:
            i +=1
            bookName = item["title"]
            price = random.randint(20, 300) * 1000
            try:
                catId = item["subjects"][0]
            except:
                catId = ""
            try:
                author = item["authors"][0]
            except:
                author = ""
            try:
                pubId = item["publisher"]
            except:
                pubId = ""
            try:
                pubYear =  item["date_published"]
            except:
                pubYear = ""
            languageId = ""
            location = fake.military_apo()
            quantity = random.randint(3, 20)
            availQuantity = quantity
            
            printRecordToXLS(i, bookName, price, catId, author, pubId, pubYear, languageId, location, quantity, availQuantity)

        workbook.save(outputfile)


for i in range(39, 100):
    book_query(fake.word(ext_word_list=None), i)