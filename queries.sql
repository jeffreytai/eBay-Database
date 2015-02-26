-- Problem 1

SELECT COUNT(*)
FROM User;

-- Problem 2

SELECT COUNT(*)
FROM User U, Item I
WHERE U.UserID = I.SellerID
	AND Binary U.Location='New York';

-- Problem 3

SELECT COUNT(*)
FROM (SELECT ItemID
		FROM ItemCategory
		GROUP BY ItemID
		HAVING COUNT(CategoryID) = 4) A;

-- Problem 4

SELECT ItemID
FROM Item
WHERE Currently = (SELECT MAX(Currently)
					FROM Item
					WHERE Ends > "2001-12-20 00:00:01"
						AND Number_of_bids > 0)
	AND Ends > "2001-12-20 00:00:01"
	AND Number_of_bids > 0;

-- Problem 5

SELECT COUNT(*)
FROM User
WHERE UserID IN (SELECT SellerID
				FROM Item)
	AND Rating > 1000;

-- Problem 6

SELECT COUNT(*)
FROM User
WHERE UserID IN (SELECT SellerID FROM Item)
	AND UserID IN (SELECT BuyerID FROM Bids);

-- Problem 7

SELECT COUNT(DISTINCT CategoryID)
FROM ItemCategory C
WHERE C.ItemID IN (SELECT ItemID
					FROM Item
					WHERE Currently>100.00
						AND Number_of_bids > 0);