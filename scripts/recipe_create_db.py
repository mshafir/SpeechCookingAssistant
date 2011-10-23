import sqlite3
from recipe_download import *

def create_db():
    db = sqlite3.connect('recipe.db')
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
            Yield           REAL
        )""")
    db.execute("""
        CREATE TABLE Instructions
        (
            RecipeID    INTEGER,
            Step        INTEGER,
            Instruction TEXT
        )""")
    #questions table to come
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

def test_insert():
    db = sqlite3.connect('recipe.db')
    db.execute('INSERT INTO Categories (Name) VALUES (?)',
               ['Test'])
    db.execute('INSERT INTO Recipes (Title,Yield) VALUES (?,?)',
               ['Test Recipe',2])
    db.execute('INSERT INTO RecipeCategories (CategoryID,RecipeID) VALUES (?,?)',
               [1,1])
    db.execute('INSERT INTO Instructions (RecipeID,Step,Instruction) VALUES (?,?,?)',
               [1,1,'Do the first thing you have to do.'])
    db.execute('INSERT INTO Instructions (RecipeID,Step,Instruction) VALUES (?,?,?)',
               [1,2,'Do the second thing.'])
    db.execute('INSERT INTO Instructions (RecipeID,Step,Instruction) VALUES (?,?,?)',
               [1,3,'Mix it all together.'])
    db.execute('INSERT INTO Instructions (RecipeID,Step,Instruction) VALUES (?,?,?)',
               [1,4,'You are done!'])
    db.execute('INSERT INTO Ingredients (Name,Alias1) VALUES (?,?)',
               ['sugar','sweetener'])
    db.execute('INSERT INTO Ingredients (Name) VALUES (?)',
               ['flour'])
    db.execute('INSERT INTO Ingredients (Name,Alias1) VALUES (?,?)',
               ['butter','margerine'])
    db.execute('INSERT INTO Ingredients (Name,Alias1) VALUES (?,?)',
               ['vanila extract','vanila'])
    db.execute("""INSERT INTO RecipeIngredients (RecipeID,
                IngredientID,IngredientDetail,Amount,Unit) VALUES (?,?,?,?,?)""",
               [1,1,'',2,'c'])
    db.execute("""INSERT INTO RecipeIngredients (RecipeID,
                IngredientID,IngredientDetail,Amount,Unit) VALUES (?,?,?,?,?)""",
               [1,2,'sifted',3,'c'])
    db.execute("""INSERT INTO RecipeIngredients (RecipeID,
                IngredientID,IngredientDetail,Amount,Unit) VALUES (?,?,?,?,?)""",
               [1,3,'softened',2,'tb'])
    db.execute("""INSERT INTO RecipeIngredients (RecipeID,
                IngredientID,IngredientDetail,Amount,Unit) VALUES (?,?,?,?,?)""",
               [1,4,'',1,'ts'])
    db.commit()
    c = db.cursor()
    c.execute('SELECT * FROM Categories')
    for x in c:print x
    c.execute('SELECT * FROM Recipes')
    for x in c:print x
    db.close()

def fill_categories():
    cats = get_categories()
    db.connect('recipe.db')
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
