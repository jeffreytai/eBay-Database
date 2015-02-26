# eBay-Database

Parse eBay data from XML files to insert into database. The database contains relational tables in Boyce-Codd normal form.

Relational tables -

Item(ItemID*, name, description, currently, buy_price, first_bid, number_of_bids, SellerID, started, ends)
ItemCategory(ItemID*, CategoryID*)

Category(CategoryID*, CategoryName)

Bids(ItemID*, BuyerID*, time*, amount)

User(UserID*, rating, location, country)

Locate(location*, country*, latitute, longitude)
