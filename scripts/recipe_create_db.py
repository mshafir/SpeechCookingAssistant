import sqlite3
from recipe_download import *

def create_db():
    db = sqlite3.connect('recipe.db')
    db.execute("""
        CREATE TABLE Categories
        (
            ID      INTEGER PRIMARY KEY AUTOINCREMENT,
            Name    TEXT,
            Display INTEGER DEFAULT 0
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
               ['butter','margarine'])
    db.execute('INSERT INTO Ingredients (Name,Alias1) VALUES (?,?)',
               ['vanilla extract','vanilla'])
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
    db = sqlite3.connect('recipe.db')
    for c in cats:
        db.execute('INSERT INTO Categories (Name) VALUES (?)',[c[1]])
    db.commit()
    db.close()
    
def fill_recipe(db,recipe):
    #handle main recipe stuff
    db.execute('INSERT INTO Recipes (Title,Yield) VALUES (?,?)',
               [recipe.title,recipe.servings])
    c = db.execute('SELECT ID FROM Recipes WHERE Title=?',[recipe.title])
    rid = c.fetchone()[0]
    #handle categories
    for c in recipe.categories:
        cursor = db.execute('SELECT ID FROM Categories WHERE Name=?',[c])
        try:
            cid = cursor.fetchone()[0]
            db.execute("""INSERT INTO RecipeCategories (CategoryID,RecipeID)
                          VALUES (?,?)""",
                       [cid,rid])
            db.commit()
        except:
            print 'could not find category '+c
    #handle ingredients (TODO:account for alt_units and measures)
    for i in recipe.ingredients:
        #does the ingredient exist?
        c = db.execute("""SELECT ID FROM Ingredients
                          WHERE Name=? OR Alias1=? OR 
                              Alias2=? OR Alias3=?""",
                       [i.ingredient,i.ingredient,i.ingredient,i.ingredient])
        results = [x[0] for x in c]
        if len(results) > 0: #exists
            iid = results[0]
        else: #doesn't exist
            db.execute("""INSERT INTO Ingredients (Name) VALUES (?)""",
                       [i.ingredient])
            c = db.execute("""SELECT ID FROM Ingredients
                            WHERE Name=?""",[i.ingredient])
            iid = c.fetchone()[0]
        db.execute("""INSERT INTO RecipeIngredients
                    (RecipeID,IngredientID,IngredientDetail,Amount,Unit)
                    VALUES (?,?,?,?,?)""",
                   [rid,iid,i.detail,i.measure,i.unit])
        db.commit()
    #handle steps
    num = 1
    for s in recipe.steps:
        db.execute("""INSERT INTO Instructions (RecipeID,Step,Instruction)
                    VALUES (?,?,?)""",[rid,num,s])
        db.commit()
        num += 1

#create_db()
#fill_categories()
cats = get_categories()
recipes = get_recipes_for_category(cats[22][0])
db = sqlite3.connect('recipe.db')
