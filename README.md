# GenMyInstance [![Build Status](https://travis-ci.org/tdurieux/genMyInstance.svg?branch=master)](https://travis-ci.org/tdurieux/genMyInstance)

```
Dorian Burihabwa
Thomas Durieux 
14 janvier 2015
```

GenMyInstance est une bibliothèque qui permet de générer toutes les instances d'une classe en fonction des ses attributs.

# Pré-Requis
- Java 7
- Alloy


# Usage
- Compiler la bibliothèque grâce à Maven ou la télécharger sur [Github](https://github.com/tdurieux/genMyInstance/releases/download/0.0.1/GenMyInstance-0.0.1-with-dependencies.jar)
```Bash
mvn install
mvn compile
```
- Ajouter le Jar à votre projet
- Générer les instances de vos classes (voir l'exemple d'utilisation)

# Exemple d'utilisation

```Java
InstanceFactory<User> instanceF = new InstanceFactory<User>(User.class);
Iterator<User> it = instanceF.iterator();
while (it.hasNext()) {
    User user = it.next();
    // do something with the user
}
```