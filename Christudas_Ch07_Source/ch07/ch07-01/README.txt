D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data\db

D:\binil\gold\shuffle\pack02\ch07\ch07-01>mvn clean package -Dmaven.test.skip=true

D:\binil\gold\shuffle\pack02\ch07\ch07-01>java -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

D:\binil\gold\shuffle\pack02\ch07\ch07-01>mvn spring-boot:run


binils-MacBook-Pro:~ mike$ curl http://localhost:8080/
{
  "_links" : {
    "categories" : {
      "href" : "http://localhost:8080/categories{?page,size,sort}",
      "templated" : true
    },
    "products" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile"
    }
  }
}

binils-MacBook-Pro:~ mike$ curl http://localhost:8080/products
{
  "_embedded" : {
    "products" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/products"
    },
    "search" : {
      "href" : "http://localhost:8080/products/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}

binils-MacBook-Pro:~ mike$ curl -i -X POST -H "Content-Type:application/json" -d '{"name":"Giomi", "code":"GIOME-KL", "title":"Giome 10 inch gold", "imgUrl":"giome.jpg", "price":11000.0, "productCategoryName":"Mobile"}' http://localhost:8080/products
HTTP/1.1 201 
Location: http://localhost:8080/products/595ce0f073ed92061ca85665
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 05 Jul 2017 12:52:00 GMT

{
  "name" : "Giomi",
  "code" : "GIOME-KL",
  "title" : "Giome 10 inch gold",
  "description" : null,
  "imgUrl" : "giome.jpg",
  "price" : 11000.0,
  "productCategoryName" : "Mobile",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    },
    "product" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    }
  }
}

binils-MacBook-Pro:~ mike$ curl http://localhost:8080/products
{
  "_embedded" : {
    "products" : [ {
      "name" : "Giomi",
      "code" : "GIOME-KL",
      "title" : "Giome 10 inch gold",
      "description" : null,
      "imgUrl" : "giome.jpg",
      "price" : 11000.0,
      "productCategoryName" : "Mobile",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        },
        "product" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/products"
    },
    "search" : {
      "href" : "http://localhost:8080/products/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}

binils-MacBook-Pro:~ mike$ curl -i -X PUT -H "Content-Type:application/json" -d '{"name":"Giomi-New", "code":"GIOME-KL-NEW", "title":"Giome New 10 inch gold", "imgUrl":"giomenew.jpg", "price":15000.0, "productCategoryName":"Mobile"}' http://localhost:8080/products/595ce0f073ed92061ca85665
HTTP/1.1 200 
Location: http://localhost:8080/products/595ce0f073ed92061ca85665
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 05 Jul 2017 12:53:27 GMT

{
  "name" : "Giomi-New",
  "code" : "GIOME-KL-NEW",
  "title" : "Giome New 10 inch gold",
  "description" : null,
  "imgUrl" : "giomenew.jpg",
  "price" : 15000.0,
  "productCategoryName" : "Mobile",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    },
    "product" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    }
  }
}

binils-MacBook-Pro:~ mike$ curl http://localhost:8080/products
{
  "_embedded" : {
    "products" : [ {
      "name" : "Giomi-New",
      "code" : "GIOME-KL-NEW",
      "title" : "Giome New 10 inch gold",
      "description" : null,
      "imgUrl" : "giomenew.jpg",
      "price" : 15000.0,
      "productCategoryName" : "Mobile",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        },
        "product" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/products"
    },
    "search" : {
      "href" : "http://localhost:8080/products/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}

binils-MacBook-Pro:~ mike$ curl -i -X PATCH -H "Content-Type:application/json" -d '{"price":15000.50}' http://localhost:8080/products/595ce0f073ed92061ca85665
HTTP/1.1 200 
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 05 Jul 2017 12:54:49 GMT

{
  "name" : "Giomi-New",
  "code" : "GIOME-KL-NEW",
  "title" : "Giome New 10 inch gold",
  "description" : null,
  "imgUrl" : "giomenew.jpg",
  "price" : 15000.5,
  "productCategoryName" : "Mobile",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    },
    "product" : {
      "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
    }
  }
}

binils-MacBook-Pro:~ mike$ curl http://localhost:8080/products
{
  "_embedded" : {
    "products" : [ {
      "name" : "Giomi-New",
      "code" : "GIOME-KL-NEW",
      "title" : "Giome New 10 inch gold",
      "description" : null,
      "imgUrl" : "giomenew.jpg",
      "price" : 15000.5,
      "productCategoryName" : "Mobile",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        },
        "product" : {
          "href" : "http://localhost:8080/products/595ce0f073ed92061ca85665"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/products"
    },
    "search" : {
      "href" : "http://localhost:8080/products/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}

binils-MacBook-Pro:~ mike$ curl -i -X DELETE http://localhost:8080/products/595ce0f073ed92061ca85665
HTTP/1.1 204 
Date: Wed, 05 Jul 2017 12:55:40 GMT

binils-MacBook-Pro:~ mike$ curl http://localhost:8080/products
{
  "_embedded" : {
    "products" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/products{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/products"
    },
    "search" : {
      "href" : "http://localhost:8080/products/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}
binils-MacBook-Pro:~ mike$ 

