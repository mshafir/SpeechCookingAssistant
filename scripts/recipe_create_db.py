import sqlite3
from recipe_download import *

def create_db():
    db = sqlite3.connect('RecipeDB')
    db.execute("""
        CREATE TABLE Categories
        (
            ID      INTEGER PRIMARY KEY AUTOINCREMENT,
            Name    TEXT
        )""")
    db.execute("""
        CREATE TABLE RecipeCategories
        (
            CategoryID  INTEGER,
            RecipeID    INTEGER
        )""")
    db.execute("""
        CREATE TABLE Recipes
        (
            ID        INTEGER PRIMARY KEY AUTOINCREMENT,
            Title           TEXT,
            Yield           REAL,
            Instructions    TEXT
        )""")
    db.execute("""
        CREATE TABLE Ingredients
        (
            ID      INTEGER PRIMARY KEY AUTOINCREMENT,
            Name    TEXT,
            Alias1  TEXT,
            Alias2  TEXT,
            Alias3  TEXT
        )""")
    db.execute("""
        CREATE TABLE RecipeIngredients
        (
            RecipeID         INTEGER,
            IngredientID     INTEGER,
            IngredientDetail TEXT,
            Amount           REAL,
            Unit             TEXT
        )""")
    db.commit()
    db.close()

def fill_categories():
    cats = get_categories()
    db.connect('RecipeDB')
    for c in cats:
        db.execute('INSERT INTO Categories (Name) VALUES (?)',[c[1]])
    db.commit()
    db.close()

def split_ingredient(text):
    return [text,'']
    
def fill_recipe(db,recipe_name,recipe_list):
    #handle main recipe stuff
    db.execute('INSERT INTO Recipes (Title,Yield,Instructions) VALUES (?,?,?)',
               [recipe_name,recipe_list[1],recipe_list[3]])
    c = db.execute('SELECT ID FROM Recipes WHERE Title=?',[recipe_name])
    rid = c.fetchone()[0]
    #handle categories
    for c in categories:
        c = db.execute('SELECT ID FROM Categories WHERE Name=?',[c])
        cid = c.fetchone()[0]
        db.execute("""INSERT INTO RecipeCategories (CategoryID,RecipeID)
                      VALUES (?,?)""",
                   [cid,rid])
        db.commit()
    #handle ingredients (TODO:account for alt_units and measures)
    for i in recipe_list[2]:
        #does the ingredient exist?
        [iname,idetail] = split_ingredient(i['ingredient'])
        c = db.execute("""SELECT ID FROM Ingredients
                          WHERE Name=? OR Alias1=?
                              Alias2=? OR Alias3=?""",
                       [iname,iname,iname,iname])
        results = [x[0] for x in c]
        if len(results) > 0: #exists
            iid = results[0]
        else: #doesn't exist
            db.execute("""INSERT INTO Ingredients (Name) VALUES (?)""",
                       [iname])
            c = db.execute("""SELECT ID FROM Ingredients
                            WHERE Name=?""",[iname])
            iid = c.fetchone()[0]
        db.execute("""INSERT INTO RecipeIngredients
                    (RecipeID,IngredientID,IngredientDetail,Amount,Unit)
                    VALUES (?,?,?,?,?)""",
                   [rid,iid,idetail,i['measure'],i['unit']])
        db.commit()
