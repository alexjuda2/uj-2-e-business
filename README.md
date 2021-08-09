# e-business

This repo contains a full stack app:
- backend - server-rendered pages + REST API. Pushes to `master` trigger deploys to https://uj-ebiz-aj.azurewebsites.net/
- frontend - SPA. Pushes to `master` trigger deploys to https://ashy-dune-057e0f003.azurestaticapps.net/

Images are available on dockerhub:
- [bare toolchain](https://hub.docker.com/repository/docker/alexjudauj/e-business-s1)
- [toolchain + backend app](https://hub.docker.com/repository/docker/alexjudauj/ebiz-backend)

Sonar overview is available at: https://sonarcloud.io/dashboard?id=alexjuda2_uj-2-e-business


# REST resources

1. Product
2. (product) Category
3. Currency
4. Review
5. CartItem
6. Order
7. OrderItem
8. Promotion
9. ProductPromotion
10. WishList
11. WishListItem
 

# Backend app

## Building for prod

```
cd backend
sbt dist
```

A `.zip` with a self-contained `.jar` inside should appear at `backend/target/universal/backend-1.0.zip`.

## Derivations

- overall project scaffold + server-rendered pages - https://github.com/kprzystalski/ebiznes2021/tree/9e838bbdeb72c8d7cc76e04af53e04eb177b9c5c/play-crud-example
- JSON API - https://github.com/kprzystalski/ebiznes2021/tree/master/oauth2/backend
- OAuth setup - https://github.com/kprzystalski/ebiznes2021/tree/55158d9cbdf15b34453945eba21aac12ff9083d3/oauth2/backend

# Frontend app

## Running locally

```
cd frontend
npm run build:dev
npm run serve
```

## Building for prod

```
npm run build:prod
```

The output should appear at `frontend/build/prod/`.

