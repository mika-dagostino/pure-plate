SELECT * 
FROM Orders 
WHERE DELIVERYDRIVERID = ? AND STATUS != 'delivered' AND STATUS != 'cancelled';