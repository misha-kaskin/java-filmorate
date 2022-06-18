# 10 спринт
![alt text](https://psv4.vkuseraudio.net/s/v1/d/i1CPnTkxgcnOQJtWqqi5qqO7otCnbVw0dZthwWnFIqsUGMQVtnBn8URlrXerLWD-0NcgxgW5j1DSct9lFrPrjIBlEM9sCswcje29sTGKbr52Eu3wEehrtg/Untitled_4.png)
# Примеры запросов для основных операций приложения:
* #### Получение всех пользователей:
```roomsql
  SELECT *
  FROM users
```
* #### Получение пользователя с id *i*:
```roomsql
  SELECT *
  FROM users
  WHERE id = i
```
* #### Получение всех фильмов:
```roomsql
  SELECT *
  FROM films
```
* #### Получение фильма с id *i*:
```roomsql
  SELECT *
  FROM films
  WHERE id = i  
```
* #### Получение списка друзей пользователя с id *i*:
```roomsql
  SELECT *
  FROM users
  WHERE id IN (SELECT to_id
               FROM friends
               WHERE from_id = i AND status = TRUE);
```
* #### Получение списка общих друзей для пользователей с id *i* и *j*:
```roomsql
  SELECT *
  FROM users
  WHERE id IN (SELECT to_id
               FROM friends
               WHERE from_id = i AND status = TRUE AND to_id IN (SELECT to_id
                                                                 FROM friends
                                                                 WHERE from_id = j AND status = TRUE))

```
* #### Получение топ *N* популярных фильмов:
```roomsql
  SELECT film_id
  FROM likes
  GROUP BY film_id
  ORDER BY count(user_id) DESC
  LIMIT N;
```