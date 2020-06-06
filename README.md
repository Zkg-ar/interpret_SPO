## interpret_SPO
### Примеры
#### Пример 1 c неограниченным количеством скобок.
   ``` Java
  
  var b = 10 + (  7 * (  a * ( 8 + ( a + 9 ) ) ) ) ;
  ```
  ```
 "Нет такой переменной"
  ```
  #### Пример 2 c неограниченным количеством скобок.
   ``` Java
  var a = 10 ;
  var b = 10 + (  7 * (  a * ( 8 + ( a + 9 ) ) ) ) ;
  ```
  ```
a = 10
b = 1900
  ```
  
#### Пример 3 c работой условий.
   ``` Java
          var operand = 600 ;
var check = 67 ;

IF operand < check BEGIN

   operand = 123 ;

ELSE

    IF check > 23 BEGIN

        check = 2 ;
    ELSE
        check = 0 ;
    END

    operand = 90 ;
END
  ```
  ```
operand = 90 
 check = 2
  ```
  #### Пример 4 c работой циклов.
```Java
var a = 10 ;
var b = 8 ;
WHILE a < 100
    BEGIN
        IF a < 50
            BEGIN
                a = ( ( ( ( a * 2 ) * 2 ) + 2 ) + 1 ) ;
            ELSE
                a = a + b * 3 ;
            END
    END
 ```
 ```
a = 175
b = 8           
 ```
  
#### Пример 5 с работой цикла DO-WHILE
```Java
var a = 10 ;
var b = 1200 ;
DO BEGIN
    a = a * 12 ;
    b = b / a ;
END WHILE a < 20 ;
```
```
a = 120
b = 10
```  
