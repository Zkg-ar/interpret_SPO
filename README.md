## interpret_SPO
### Примеры
#### Пример 1 c неограниченным количеством скобок.
   ``` Java
  
  var b = 10 + (  7 * (  a * ( 8 + ( a + 9 ) ) ) ) ;
  ```
  ```
  Результат:Нет такой переменной
  ```
  #### Пример 2 c неограниченным количеством скобок.
   ``` Java
  var a = 10 ;
  var b = 10 + (  7 * (  a * ( 8 + ( a + 9 ) ) ) ) ;
  ```
  ```
  Результат:a = 10
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
  Результат:operand = 90
            check = 2

  ```
  
  
