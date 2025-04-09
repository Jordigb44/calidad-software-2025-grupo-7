# Informe de cobertura

Cobertura de instrucciones obtenida: 29%
Cobertura de ramas obtenida: 27%

Es decir, solo un 29% de todas las instrucciones del proyecto se han probado mediante test. 
- No se han ejecutado 1209 instrucciones de 1715.
A su vez, solo un 27% de las condiciones lógicas han sido cubiertas en las pruebas.
- No se han cubierto 52 ramas de 72.

Esto se debe a que en la práctica no se han probado todas las funcionalidades y posibilidades. Por ejemplo no ha
habido ningún test de Review ni se ha probado a actualizar otros parámetros de la película que no fuese el título.

### ¿Qué clases/métodos crees que faltan por cubrir con pruebas? 

- Se deben priorizar:
Controladores, servicios, métodos utilitarios, condicionales con probabilidad de error, etc.

1. es.codeurjc.web.nitflex.controller.web
- Cobertura: 1% instrucciones, 0% ramas. Controlador Film y User.
- Prácticamente no se prueba nada de este paquete.
- Se necesitan más pruebas de integración con Mock, WebTestClient, o tests funcionales. 
- En esta práctica, se deberían haber empleado y probado los controller web en vez de usar tanto los servicios.

2. es.codeurjc.web.nitflex.model
- Cobertura: 35% instrucciones, 11% ramas
- Dado que las clases modelo incluyen lógica como validaciones, equals, toString, hashCode, etc., deberían cubrirse con más tests.
- También conviendría probar su uso en casos de éxito y error en servicios.
- El porcentaje bajo se debe principalmente a que no se prueba la clase Review en los test.

3. es.codeurjc.web.nitflex.utils
- Cobertura: 9% instrucciones, 33% ramas
- Dado que en este paquete solo se prueban las imágenes de las películas y el resto de utils (AgeRating) se ignora,
el porcentaje es muy bajo y habría que probar el resto de utils.

### ¿Qué clases/métodos crees que no hace falta cubrir con pruebas? 

- Se puede prescindir de probar:
Clases de configuración sin lógica, excepciones simples, métodos triviales como getters y setters.

**Excepcion simple:** es.codeurjc.web.nitflex.service.exceptions
- Está 100% cubierto y solo tiene 2 líneas, entonces no requiere más pruebas específicas.

**Clases de configuración:** es.codeurjc.web.nitflex.configuration
- Cobertura actual: 39%, son clases con anotaciones como @Configuration, @Bean, @Enable..., etc.

**Clases de arranque:** es.codeurjc.web.nitflex
- Cobertura baja (9%). Clase DataInitializer y Application.
- Debido a que Application solo arranca la aplicación, no tiene valor hacer un test para ello,
ya se prueba cada vez que se lanza que funciona correctamente. No aportaría ningún valor.
- Dado que DataInitializer solo se ejecuta al arrancar la app, es difícil probarla directamente 
y tampoco implementa ninguna lógica que se pueda probar, al igual que la clase Application.
