## Receipt Validation Web Service
When we sell digital goods in our iOS application, Apple will handle the payment portion through iTunes.  However, the delivery of the purchased assets will come from our own servers.  We will need to implement a system that receives an Apple receipt from the application, validates the receipt with Apple, maps the receipt to one or more unique products and delivers unique download keys to their corresponding digital assets to the app for retrieval through the Asset Download web service (see below).  

Special care should be taken to ensure that not everyone with access to the digital asset’s URL is able to download the asset.  Therefore, a timeout/expiry system for the receipt will need to be implemented such that a given URL will not be valid after a certain time period and the app will need to repeat the receipt validation step.

Another thing to note is that a receipt may map to multiple products since it’s possible to sell a bundle of products in the app.  Also, because each product can have one of 3 prices depending on the credentials (or lack thereof) of the app user, a given downloadable asset will need to be mapped to 3 distinct products in iTunes.

The service will take, as input, an Apple receipt, Base64 encoded.  The service will do the following:

1. Check a local database for the existence of the receipt.  If one can be found, look at the corresponding expiration date.  If the receipt has not expired, then do the following:
   1. For each product that is unlocked by the receipt, encrypt the receipt and the unique product key to create a corresponding unique download key.  Store the download key in the receipt_products table.
   1. Create and return a JSON response containing an array of {remote product id, download key} pairs.
2. If the receipt cannot be found in a local database, or the validation has expired, then do the following:
   1. Validate the receipt with the Apple receipt validation service (see Appendix B,C).
   1. If the receipt is not valid, return a JSON response with an empty array and status=fail.
   1. If the receipt is valid, then: 
      1. Store the receipt and the validation expiration time in the receipts table.
      1. Store the unlocked product IDs in the receipt_products table.
      1. For each product that is unlocked by the receipt, encrypt the receipt and the unique product key to create a corresponding unique download key.  Store the download key in the receipt_products table.
      1. Create and return a JSON response containing an array of {remote product id, download key} pairs.

The call to this service will need to be made over SSL to prevent the receipt data from being intercepted.

##### Definition

Input Parameters:

    { "receipt-data": "<an Apple-provided receipt string>" }
    

Output:

    { "status": "[ok | fail]",
      "products": [ { "product_id": "<product ID>", "download_key": "<download key>" }, ...]
    }
* status: either "ok" or "fail".
* products: an array of {product id, download key} pairs, if status=”ok”.




### Receipt Verification with the Apple App Store

To verify a receipt with the Apple App Store, you will need to do the following:

1. Create a JSON object with a single key named receipt-data whose value is the string received in the receipt-data parameter of the validate-receipt service.
   ```
    {
	     “receipt-data” : “(actual receipt bytes here)”
    }    
   ```
1. Post the JSON object to the App Store using an HTTP POST request.  The URL for the store is: https://buy.itunes.apple.com/verifyReceipt

    Note: during testing, we need to post this JSON object to the following test URL instead.  Everything else remains the same:

    https://sandbox.itunes.apple.com/verifyReceipt

1. The response received from the App Store is a JSON object with two key, status and receipt.  It should look like this:
   ```
    {
	    “status” : 0
	    “receipt”: { … }
    }    
   ```
   If the value of the status key is 0, then the receipt is valid.  For any other value, the receipt is invalid.

1. The value of the “receipt” key is a JSON dictionary containing several key/value pairs.  Of those, “product_id” will need to be queried to get additional information about the purchased product.  The value uniquely identifies the purchased product and will correspond to a unique entry in our products table.
