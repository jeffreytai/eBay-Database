/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

import java.lang.Object;
import java.util.Map.Entry;
import java.util.Date;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    public static class Locate {
        String itemID;
        String location;
        String country;
        String latitude;
        String longitude;

        Locate(String itemID, String location, String country, String latitude, String longitude) {
            this.itemID = itemID;
            this.location = location;
            this.country = country;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static class User {
        String userID;
        String rating;
        String location;
        String country;

        User(String userID, String rating, String location, String country) {
            this.userID = userID;
            this.rating = rating;
            this.location = location;
            this.country = country;
        }
    }

    public static class Item {
        String itemID;
        String name;
        String description;
        String currently;
        String buy_price;
        String first_bid;
        String number_of_bids;
        String sellerID;
        String started;
        String ends;

        Item(String itemID, String name, String description, String currently, String buy_price, String first_bid, String number_of_bids, String sellerID, String started, String ends) {
            this.itemID = itemID;
            this.name = name;
            this.description = description;
            this.currently = currently;
            this.buy_price = buy_price;
            this.first_bid = first_bid;
            this.number_of_bids = number_of_bids;
            this.sellerID = sellerID;
            this.started = started;
            this.ends = ends;
        }
    }

    public static class Bids {
        String itemID;
        String buyerID;
        String time;
        String amount;

        Bids(String itemID, String buyerID, String time, String amount) {
            this.itemID = itemID;
            this.buyerID = buyerID;
            this.time = time;
            this.amount = amount;
        }
    }

    public static class Category {
        int categoryID;
        String categoryName;

        Category(int categoryID, String categoryName) {
            this.categoryID = categoryID;
            this.categoryName = categoryName;
        }
    }

    public static class ItemCategory {
        String itemID;
        int categoryID;

        ItemCategory(String itemID, int categoryID) {
            this.itemID = itemID;
            this.categoryID = categoryID;
        }
    }

    static Map<String, Locate> locationHash = new HashMap<String, Locate>();
    static Map<String, User> userHash = new HashMap<String, User>();
    static Map<String, Item> itemHash = new HashMap<String, Item>();
    static Map<String, Bids> bidsHash = new HashMap<String, Bids>();
    static Map<String, Category> categoryHash = new HashMap<String, Category>();
    static Map<String, ItemCategory> itemCategoryHash = new HashMap<String, ItemCategory>();
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }

    static String XMLtoSQLDate(String date) {
        String formatted = "";
        SimpleDateFormat sd = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
        try {
            Date sqlFormat = sd.parse(date);
            sd.applyPattern("yyyy-MM-dd HH:mm:ss");
            formatted = sd.format(sqlFormat);
        } catch (Exception e) {
            System.out.println("Error in re-formatting date");
        }

        return formatted;
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        Element rootElem = doc.getDocumentElement();
        Element[] item = getElementsByTagNameNR(rootElem, "Item");

        for (int x=0; x<item.length; x++) {
            // all singular items
            String itemID = item[x].getAttribute("ItemID");
            String name = getElementTextByTagNameNR(item[x], "Name");
            String currently = strip(getElementTextByTagNameNR(item[x], "Currently"));
            String buy_price = strip(getElementTextByTagNameNR(item[x], "Buy_Price"));
            String first_bid = strip(getElementTextByTagNameNR(item[x], "First_Bid"));
            String number_of_bids = getElementTextByTagNameNR(item[x], "Number_of_Bids");

            // seller info
            Element seller = getElementByTagNameNR(item[x], "Seller");
            String seller_rating = seller.getAttribute("Rating");
            String seller_userID = seller.getAttribute("UserID");
            Element s_location = getElementByTagNameNR(item[x], "Location");
            String seller_location = getElementTextByTagNameNR(item[x], "Location");
            String latitude = s_location.getAttribute("Latitude");
            String longitude = s_location.getAttribute("Longitude");
            String country = getElementTextByTagNameNR(item[x], "Country");
            String started = XMLtoSQLDate(getElementTextByTagNameNR(item[x], "Started"));
            String ends = XMLtoSQLDate(getElementTextByTagNameNR(item[x], "Ends"));
            String description = getElementTextByTagNameNR(item[x], "Description");
            if (description.length() > 4000) {
                description = description.substring(0, 4000);
            }

            // checked
            Item itemObject = new Item(itemID, name, description, currently, buy_price, first_bid, number_of_bids, seller_userID, started, ends);
            itemHash.put(itemID, itemObject);

            if (!userHash.containsKey(seller_userID)) {
                User userObject = new User(seller_userID, seller_rating, seller_location, country);
                userHash.put(seller_userID, userObject);
            }

            // add seller location info
            // checked
            if (latitude == "") {
                Locate locationObject = new Locate(itemID, seller_location, country, null, null);
                if ( (seller_location != "") && (country != "") ) 
                    locationHash.put(itemID, locationObject);
            } else {
                Locate locationObject = new Locate(itemID, seller_location, country, latitude, longitude);
                if ( (seller_location != "") && (country != "") ) 
                    locationHash.put(itemID, locationObject);
            }
            
            // possibly multiple elements
            Element[] categories = getElementsByTagNameNR(item[x], "Category");
            for (int i=0; i<categories.length; i++) {
                int catID;
                String catName = getElementText(categories[i]);
                if (categoryHash.containsKey(catName)) {
                    Category catObject = categoryHash.get(catName);
                    catID = catObject.categoryID;
                } else {
                    // checked
                    catID = categoryHash.size() + 1;
                    Category categoryObject = new Category(catID, catName);
                    categoryHash.put(catName, categoryObject);
                }

                // checked
                ItemCategory itemCategoryObject = new ItemCategory(itemID, catID);
                itemCategoryHash.put(itemID + catID, itemCategoryObject);
            }

            Element bids = getElementByTagNameNR(item[x], "Bids");
            Element[] bid = getElementsByTagNameNR(bids, "Bid");

            for (int i=0; i<bid.length; i++) {
                Element bidder = getElementByTagNameNR(bid[i], "Bidder");
                String bidder_rating = bidder.getAttribute("Rating");
                String bidder_UserID = bidder.getAttribute("UserID");
                String bidder_location = getElementTextByTagNameNR(bidder, "Location");
                String bidder_country = getElementTextByTagNameNR(bidder, "Country");
                String bid_time = XMLtoSQLDate(getElementTextByTagNameNR(bid[i], "Time"));
                String bid_amount = strip(getElementTextByTagNameNR(bid[i], "Amount"));

                // checked
                Bids bidObject = new Bids(itemID, bidder_UserID, bid_time, bid_amount);
                bidsHash.put(bidder_UserID + bid_time, bidObject);

                // checked
                if (!userHash.containsKey(bidder_UserID)) {
                    User userObject2 = new User(bidder_UserID, bidder_rating, bidder_location, bidder_country);
                    userHash.put(bidder_UserID, userObject2);
                }
            }
        }
        /**************************************************************/
        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        // write to Locate.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Locate.del")));
            
            Iterator it = locationHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Locate l = (Locate)pair.getValue();

                String data = String.format("%s |*| %s |*| %s |*| %s |*| %s\n",
                                            l.itemID,
                                            l.location,
                                            l.country,
                                            l.latitude,
                                            l.longitude);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write to User.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("User.del")));

            Iterator it = userHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                User u = (User)pair.getValue();

                String data = String.format("%s |*| %s |*| %s |*| %s\n",
                                            u.userID,
                                            u.rating,
                                            u.location,
                                            u.country);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write to Item.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Item.del")));

            Iterator it = itemHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Item i = (Item)pair.getValue();

                String data = String.format("%s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s |*| %s\n",
                                            i.itemID,
                                            i.name,
                                            i.description,
                                            i.currently,
                                            i.buy_price,
                                            i.first_bid,
                                            i.number_of_bids,
                                            i.sellerID,
                                            i.started,
                                            i.ends);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write to Bids.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Bids.del")));

            Iterator it = bidsHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Bids b = (Bids)pair.getValue();

                String data = String.format("%s |*| %s |*| %s |*| %s\n",
                                            b.itemID,
                                            b.buyerID,
                                            b.time,
                                            b.amount);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write to Category.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Category.del")));

            Iterator it = categoryHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Category c = (Category)pair.getValue();

                String data = String.format("%s |*| %s\n",
                                            c.categoryID,
                                            c.categoryName);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write to ItemCategory.del
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("ItemCategory.del")));

            Iterator it = itemCategoryHash.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                ItemCategory ic = (ItemCategory)pair.getValue();

                String data = String.format("%s |*| %s\n",
                                            ic.itemID,
                                            ic.categoryID);

                pw.write(data);
            }
            it.remove();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
