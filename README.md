# e-business session 1

Date: 06-03-2021

Image contents:

- Java 1.8
- Scala 2.12.12
- sbt 1.4.7
- node 15.12.0
- npm 7.6.3

Images are available on [dockerhub](https://hub.docker.com/repository/docker/alexjudauj/e-business-s1).

Verifying image contents:

```
IMG="..."
docker run --rm -it $IMG java -version
docker run --rm -it $IMG scalac -version
docker run --rm -it $IMG node -v
docker run --rm -it $IMG npm -v
```

Veryfing `sbt` version works too, but takes a long time because `sbt` initializes a new project and downloads `.jars` only to output its own version.
```
docker run --rm -it $IMG sbt sbtVersion
```

# REST resources

1. Product
2. (product) Category
3. Currency
4. User
5. Review
6. Cart
7. Order
8. Invoice
9. Address
10. Promotion
11. WishList
 


# Backend app

## Derivations

- overall project scaffold + server-rendered pages - https://github.com/kprzystalski/ebiznes2021/tree/9e838bbdeb72c8d7cc76e04af53e04eb177b9c5c/play-crud-example
- JSON API - https://github.com/kprzystalski/ebiznes2021/tree/master/oauth2/backend
- OAuth setup - https://github.com/kprzystalski/ebiznes2021/tree/55158d9cbdf15b34453945eba21aac12ff9083d3/oauth2/backend

## Deps

- scala 2.12
- play-slick 4.0.0 
- play 2.7
- slick 3.3

