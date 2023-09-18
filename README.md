# Proyecto fin de ciclo

1\. XUSTIFICACIÓN

2\. OBXECTIVO

3\.TÉCNOLOXIAS E PROGRAMAS EMPREGADOS

4\. DETALLE DAS FUNCIÓNS E CARACTERISTICAS DE CADA PARTE DA APLICACIÓN

5\. EXECUCIÓN

6\. MELLORAS



1\. XUSTIFICACIÓN

O motivo da realización deste proxecto é que nel podo englobar gran parte dos coñecementos obtidos nas distintas materias ao longo do ciclo xa que se tocarán unha gran parte deles en maior ou

menor medida. O programa estará realizado en Java empregando JavaFX para realizar os distintos formularios dos que está composto xa que é a linguaxe que empregamos durante  o curso seguirei con ela para a realización do proxecto



2\. OBXECTIVOS

O obxectivo é o de realizar unha aplicación coa que se poida levar o control de citas, persoal e clientes dun pequeno negocio estilo perruqueira, clínica dental etc.

En el poderemos ter gardados os datos de todos os clientes cos que contamos e manter un historial de todas as citas que tivo co noso negocio. Así mesmo tamén teremos un control dos nosos empregados, podendo ter os seus datos e ver que citas atendeu cada un de eles.

Na parte de citas o que se pretende e levar un control de asignación delas, podendo ver as citas que hai asignadas en cada horario dispoñible e non permitindo asignar mais si ese horario xa está completo. Tamén se pode levar un control delas, vendo en que estado se atopan e como se resolveron (cancelouse, o usuario non se presentou, finalizouse con éxito, está pendente).


3\. TECNOLOXÍAS E PROGRAMAS EMPREGADOS

A continuación detallo as tecnoloxías e programas empregados para realizar o proxecto:

- Linguaxe de programación: O proxecto está realizado integramente en Java. En concreto a versión utilizada é Java 17. En canto a versión de Java FX é a 18.0.1. Dentro de java empregáronse varia das súas librarías para poder realizar as diferentes funcións do proxecto.
- Xestor de Proxectos: Para a xestión de proxectos e dependencias empreguei Apache Maven 

A continuación detallo as librarías que se empreguei para o proxecto:
- javafx-controls:18.0.1
- javafx-controls:win:18.0.1
- javafx-graphics:18.0.1
- javafx-graphics:win:18.0.1
- jfxtras-icalendaragenda:11-r1
- jfxtras-icalendarfx:11-r1
- org.jfxtras:jfxtras-controls:17-r1
- org.jfxtras:jfxtras-common:17-r1
- org.jfxtras:jfxtras-fxml:17-r1
- com.sun.mail:javax.mail:1.5.3
- org.hibernate:hibernate-core:5.4.32.Final
- org.hibernate:hibernate-entitymanager:5.4.15.Final
- org.hibernate.javax.persistence:hibernate-jpa-2.1-api:1.0.2.Final
- org.hibernate.common:hibernate-commons-annotations:5.1.2.Final
- org.hibernate.validator:hibernate-validator:6.0.17.Final
- javax.persistence:javax.persistence-api:2.2
- mysql:mysql-connector-java:8.0.24

- Scene Builder: Empregado para a creación de formularios e para o deseño, na súa 19.0.0

- Persistencia de Datos: para realizar a persistencia de datos dende a aplicación na base de datos empreguei JPA/Hibernate

- Folla de estilos CSS: Empregada para aplicar o mesmo estilo a elementos presentes en distintos formularios e manter a aplicación o mais uniforma posible e facilitar futuros cambios na aparencia

- IDE: empreguei IntelliJ IDEA Community Edition 2022.1.3

- Base de Datos: Para a base de datos optei por realizala empregando MySql 8.0. Empreguei MySQL Workbench 8.0 CE para o deseño e creación das táboas. A continuación mostrase un esquema da base de datos, coas taboas e as relacións entre elas

![Login](/Capturas/esquemabbdd.JPG)


4\. DETALLE DAS FUNCIÓNS E CARACTERISTICAS DE CADA PARTE DA APLICACIÓN

A aplicación principalmente está basado no patrón de deseño Modelo Vista Controlador. Onde tesmos unhas vistas que son os formularios que se mostran ao usuario e pode realizar peticións e onde se lle mostran os resultados, estas solicitudes son procesadas e tratadas nos controladores de cada un dos formularios que se encargan de obter os datos dos modelos (tablas da base de datos) e mostralos de novo na vista correspondente. A continuación mostranse os distintos paquetes e clases da aplicación:
![Login](/Capturas/estructuraaplicacion.JPG)

accesodatos: en este paquete gardamos a clase onde realziamos a conexión a BBDD e despois temos unha clase para realizar as consultas a cada taboa. Para mantelo todo en orde e saber onde está gardada cada consulta en función da taboa contra a que se realice

configuración: en este paquete gardase a clase configuracion onde se  gardan as variables para configurar a aplicación (como por exemplo os horarios das citas ou o numero máximo e citas) asi como a firma coa que se envia o correo. En melloras explicase como debería ser esta clase.

Controllers: contén as clases asociadas a cada un dos formularios que se encarga de procesar as solicitudes e obter un resultado delas para mostralo de novo no formulario ou realizar a operación solicitada

exportar: contesa clase ExportarDatos que é a que se encarga de pasar os datos das táboas a formato excel

inicio: a clase principal da aplicación

mail: conten a calse EnvioCorreo  onde esstá a configuración necesaria e onde se crean os correos que se envian dende a aplicación

modelos: contén clases para mapear as taboas da BBDD

sesion: está a clase UsuarioSesion que crea a instancia cos datos do usuario conectado

Carpeta inicio: conten en formato .fxml todos os formularios da aplicación

Carpeta images: están gardados as distintas imaxes e iconos da aplicación (Sacados da web https://www.flaticon.com/)

estilo.css : este é o arquivo onde se gardan distintos estilos que se aplicacn aos diferentes elementos da aplicación

A aplicación conta con varios formularios desde os que xestionar as diferentes funcións dispoñible. A continuación detallase cada un deles e as funcións que se realizan e como se realizan.

**LOGIN**: E o formulario de inicio da aplicación, dende el solicitase un usuario (coincide co DNI do empregado) e unha pass para poder acceder. Unha vez se introduce o usuario e o contrasinal e se preme o botón de entrar lanzase unha consulta a base de datos que comproba se usuario introducido existe e si existe si a pass que ten coincide coa introducida. Si os datos introducidos son incorrectos mostrase unha mensaxe indicando este feito, si os datos son correctos accedese a aplicación e móstrasenos o menú principal.

![Login](/Capturas/login.JPG)

Unha vez se accede a aplicación pódese acceder con 2 tipos de permisos, usuario e admin (en cada apartado mais adiante detallase as diferenzas entre un permiso e outro). Para gardar os datos do usuario que accede a aplicación temos unha clase chamada UsuarioSesón onde se o facer login se crea unha instancia cos seus datos e o rol. Está clase ten o patrón de deseño Singleton de xeito que so se pode crear unha instancia del

![Login](/Capturas/claseusuariosesion.JPG)

**MENÚ PRINCIPAL**
En este formulario temos menús incluídos en un acordeón pane formado por 4 pestanas, cada unha delas despregase e conten botóns cos que se realizan as diferentes operacións en cada apartado. Estes apartados son, que son clientes, Citas, Estatísticas, Administración.

No pe do formulario principal mostrase unha mensaxe de benvida cos datos de usuario logado e entre corchetes os permisos que ten (usuario ou admin).

Cada un destes apartados conta con varios botóns para acceder a cada unha das funcións, destes formularios so se pode ter un aberto a vez de tal xeito que si se quere abrir outro o que se fai e traer a primeiro plano a ventana en vez de abrir un novo 

A continuación detallase cada unha destas opcións e os formularios implicados en ela

![Login](/Capturas/menuprincipal.JPG)

**CLIENTES**

Dende este apartado podemos realizar as distintas operacións cos clientes da nosa aplicación. Temos dous botóns que nos dan acceso a distintos formularios, os botóns son Alta e Lista.

ALTA: Ábrese o **formulario de alta de clientes,** conta con diversos campos para engadir a información do novo cliente, estes campos son os seguintes:

-Id: xerado automaticamente (único)

-Data de alta: xerado automaticamente, data actual

-DNI: campo obrigatorio (único)

-Nome:campo obrigatorio

-Apelido1: campo obrigatorio

-Apelido2: campo obrigatorio

-Correo: obrigatorio formato típico de correo(<xxx@x.xx>)

-Telefono: so admite números

-Dirección

Restricións:

Para os campos obrigatorios si se deixan sen cubrir salta unha advertencia e resáltanse os campos e impide a creación do cliente

O mesmo ocorre si se introduce un mail, si este non cumpre o formato mostrase advertencia, resaltase o campo e non permite continuar coa creación do cliente.

![Login](/Capturas/altacliente.JPG)

LISTA: Dende o **formulario lista de clientes contamos** cos campos dos nosos clientes nos que podemos realizar o filtro de búsquela por calquera deles, a busqueda realizase por coincidencia exacta podendo empregar como comodín o % para ampliar a búsquela e que non sexa por coincidencia exacta. Unha vez cuberto algún campo si se preme o botón buscar realizase a consulta e cóbrese a táboa cos resultados.

Esta búsquela por defecto realizase dos  clientes que están de alta pero temos un checkbox que nos permite realizar a búsqueda incluído clientes dados de baixa.

O botón limpar borra o texto de todos os campos e elimina os resultados de búsqueda da táboa

Existe un botón exportar que nos permite recoller os datos da táboa e gardalos en un arquivo formato excel dentro do noso equipo

![Login](/Capturas/listaclientes.JPG)


Dentro dos resultados da táboa podemos facer dobre click en eles e abriríase unha nova pestana co **formulario datos de cliente,** onde** 

Onde vemos todos os seus datos e podemos realizar modificacións en eles así como se podería proceder a súa baixa (si o usuario está de baixa poderíase restaurar e cambiar o seu estado a activo).

Cando se realiza a baixa de un cliente todas as citas asignadas a el pasan o estado Cancelado. Neste formulario aplicanse estilos ao encabezado, aparecendo en verse se o usuario está activo e vermello se está dado de baixa
Ademais de ver os datos podemos ver unha táboa co historial de citas do cliente as que podemos consultar. Si as citas teñen un estado diferente a *Pendiente* so se abren a modo de consulta sen poder modificalas, se teñen outro estado son modificables e pódese cambiar o seu estado.
Cliente activo:

![Login](/Capturas/datosclienteactivo.JPG)

Cliente de baixa:

![Login](/Capturas/datosclientebaja.JPG)


Tamén hai dispoñible un botón de correo por si se quere enviar algunha comunicación ao cliente. Abresenos unha ventana coa dirección xa cuberta e un campo para o asunto e outro para o corpo. Si o correo se vai enviar sin asunto abrese unha dialogo preguntando si se desexa continuar co envio. O envio de correo dende aqui ademais engadese unha firma automaticamente na que figura os datos do usuario que o envia que se recollen dos datos de inicio de sesión e se xeran en un método de tal xeito que o usuario non pode impedir esto

![Login](/Capturas/correo.JPG)



**CITAS**

En este apartado contamos coas distintas opcións para realizar as distintas operacións sobre as citas (alta, cambio de estado, consulta). As opción que temos neste apartado son Crear e Listado

CREAR: En este apartado e onde se realiza a alta de novas citas dentro da nosa a aplicación empregando o **formulario seleccionar horario para cita.**

![Login](/Capturas/crearcita.JPG)


En este formulario temos un Calendarpicker onde temos un calendario no que poderemos seleccionar a data para a que queremos crear a nosa cita, este calendarpicker ten a restrición de que non se poden seleccionar datas anteriores a actual (por defecto tamén están des habilitados sábados e domingos pero esta cuestión xa dependería das necesidades do negocio onde se empregue a aplicación).

Temos unha etiqueta na que se mostra a data completa en función da seleccionada no calendario

Por outra parte temos outra área onde se xeran os distintos botóns onde se mostra as horas nas que hai citas. Estes botóns xéranse dinamicante en función de unha hora de inicio, unha hora de fin e a duración da cita. Por exemplo si a hora de inicio e as 9:00 e a duración da cita 20 minutos xeraríanse botóns para as 9:00,9:20,9:40.... así ata a hora de fin

Hai dous bloques, un para horario de mañá e outro para horario de tarde que funcionan do mesmo xeito(cada un coa súa hora de inicio e a súa hora de fin)

Estes botóns ademais teñen un estilo programado en función de si hai citas libres a esa hora(hai unha variable que limita o máximo de citas por horario), si existen horas estará en ver e si non hai estará en roxo e deshabilitado.

Estas comprobación e posterior asignación de cita realizase de dous xeitos en función dos permisos do usuario:

Permisos de USUARIO: En este caso a consulta para ver as citas libres/ocupadas realizase para o propio usuario, so ve as súas. Esto é así por que a hora de crear citas so pode asignarse citas así mesmo

Permisos de ADMIN: en este caso a consulta realizase tendo en conta todos os empleados que temos. 

![Login](/Capturas/generarbotones.JPG)


Aqui vemos como se realiza este proceso:
- Creamos un Hbox (organiza horizontalmente) e a continuación un Vbox contendo o Hbox anterior
- creamos un bucle que comeza na hora de inicio e por cada iteración sumalle os minutos da duración da cita, executase emntras a hora sexa inferior a fecha de fin do horario (mañá ou tarde)
- Creamos o boton coa fechacorrespondente
- gardamos o boton nunha lista (esto faise para borrarlle o estilo e que non se superpoña cando se actualizan)
- apliccamoslle o estilo que lle corresponde en función de se hai citas dispoñibles ou non
- agregamos o boton ao hboz e sumamos 1 ao contador. Este contador limita o número de botóns por fila (no meu caso 5 pero poden ser os que mais gusten en función do deseño)
- se o contador e 5 o que se fai e crear un novo hbox dentro do Vbox inicial, o que fai que teñamos unha nova fila na que seguir metendo botóns

Dentro deste mesmo metodo xerarBotons() tamén temos o evento que se lanza cando pulsamos unha boton das citas. O que fai e abrir unha ventana en modo modal para cubrir os datos da cita (mais adiante vemos este formulario). E cando se pecha borramos os botóns que gardamos na lista e xeramolos de novo para que se actualzicen e se mostre o estilo correctos. Esto lanzase cando cerramos a ventana de nova cita co evento setOnHide

![Login](/Capturas/abrirnuevacita.JPG.JPG)

En este código cemos como se xeran os botóns para as citas da mañá, os da tarde serían igual.

Unha vez se selecciona unhas das citas dispoñibles ábresenos o **formulario de crear cita** cos seguintes campos:

![Login](/Capturas/datoscita.JPG)

ID da cita: xerase automaticamente

Horario: o que se indicaba no botón:

Atendido por: si a cita é creado con permisos de USUARIO non hai opción a seleccionar outro empregado, con permisos de ADMIN sae a lista de empregados que non teñen xa asignada unha cita en ese horario:

Comentario: campo de texto para facer anotacións sobre a cita

Cliente: temos dispoñible unha lupa onde se nos abre un novo formulario para buscar o cliente ao que se lle vai asignar a cita, unha vez seleccionado os datos cárganse no formulario.

![Login](/Capturas/selecionarcliente.JPG)

Si o cliente xa ten asignada unha cita para ese horario non se permite asignarlla saltandonos unha advertencia

![Login](/Capturas/citayaprogramada.JPG)

Unha vez completo estes datos podemos cancelar(non se garda nada) ou gardar a cita.

Si a gardamos a cita crease na base de datos un rexistro cos datos indicados e sáenos unha mensaxe flotante indicando a creación da cita e ademais si hai un correo válido de cliente enviáselle un mail indicando que se concertou a cita e o día e hora da mesma

LISTA: Dende este apartado podemos realizar diversas consultas en función das necesidades da nosa búsqueda, esta realizase en función dos datos que teñamos seleccionados podendo buscar por rango de datas, estado e empregado asignado a cita. Ao igual que en outros formularios cotamos cos botóns de limpar para borrar todas as opción de busqueda e un botón para exportar os resultados.

![Login](/Capturas/listadocitas.JPG)


Se facemos dobre click en unha das citas ábresenos unha nova ventana onde podemos consultar e modificar os datos así como cambiar o estado da mesma con certas restricións. A cita pódese cancelar en calquera momento pero so se pode pasar a estado No Presentado ou Finalizada cando se sobre pasou a data para a que estaba fixada.

Si a cita se cancela enviáselle unha notificación ao cliente indicando que se cancelou. En este formulario tamén temos un estilo que se aplica a zona de datos en funcion do estado para ver mais rapidamente como se atopa (axul=pendente,vermello=cancelada, amarelo=no presentado, verde=finalizada)

![Login](/Capturas/vistacitas.JPG)

**CUADRO DE MANDOS**

En este apartado permítenos ver unhas estatísticas xerais da nosa aplicación así como poder analizar outras mais concretas como ver a evolución do número de citas por día durante un determinado período de tempo, o estado no que están e cales desas citas foi asignada a cada un dos empregado

Si seleccionamos un empregado en concreto podemos ver especificamente en que estado esta cada unha das súas citas asignadas e o número de citas asignadas cada un dos días dentro do período escollido

![Login](/Capturas/estadisticas.JPG)

**ADMINISTRACIÓN**
A esta parte si se accede con permisos de Administrador  pódese consultar os diferentes empregados que temos así como dar de alta novos e modificar os seus datos

No formulario de **vista de empregados** contamos a igual que como vimos en outros un primeiro formulario no que podemos realizar a consulta a táboa empregados en función dos campos seleccionados. A igual que nos demais estes datos son exportables.

![Login](/Capturas/listaempleados.JPG)

Facendo dobre click podemos acceder ao detalle de cada un dos nosos empregados e modificar datos ou dalos de baixa. Aqui tamén se aplica o estilo de fondo verde para empregados de alta e vermello se estan dados de baixa

Con permisos de usuario en vez de aparecernos o boton de usuarios apareceranos o botón de usuario e so poderemos ver e modificar o noso, non podendo cambiarnos o rol.

![Login](/Capturas/datosempleado.JPG)

5\. EXECUCIÓN

**ENTORNO**

 Desenrolado empregando o IDE IntelliJ IDEA Community Edition 2022.1.3 de descarga gratuita dende https://www.jetbrains.com/idea/download/#section=windowsintalando o IDE e descargando o proxecto dende o repositorio habería que iniciar o IDE e abrir a carpeta donde gardamos o proxecto

**BASE DE DATOS**

A base de datos empregada é MySQL. Os datos de conexión son os seguintes

Hostname: localhost

Port: 3306

Username:root

Pass: root

O nome da base de datos que se debe crear é bd_proxectofct un vez creado executase o script scriptBBDD.sql para crear as taboas e asignar uns valores inicales a alguna delas.

Creanse dous usuarios iniciales para acceder a aplicacion:

Usuario:  usuario Pass: usuario Rol:usuario

Usuario:  admin Pass: admin Rol: admin

**JAVA**

A versión de java empregada é a 17.0.3.1" 2022-04-22 LTS


6\. MELLORAS

Etre as melloras posibles melloras e as que me houbese gutado incluir son:
Un apartado de configuración: en este apartado poderiase editar as horas de inicio e fin das citas tanto da tarde como da mañá, a duración da cita asi como o máximo de citas dispoñibles por horario establecendo que o máximo sexa o número de empregados que temos. Tamén se poderia tes un apartado para crear unha firma para os correos que se envian dende a propia aplicación para por exemplo poñer os datos de contacto,direccióntelefono,etc no negocio. Para facer esto os datos da clase configuracion deberian gardarse nunha taboa da BBDD, despois creariamos un modelo para mapeala e  necesitariamos unha vista e un controlador para realizar as operacións sobre ela. Estes datos cargarianse ao incicio de sesión igual que se fai cos datos do usuario conectado
Na lista de citas incluir unha advertencia de que hai citas pasadas sin pechar. Por exemplo si chegaramos hoxe ao traballo e hai citas de onte en estado Pendiente que saira unha notificación/botón e que premendo en el xa se fixese a consulta e se mostrase cales son. Xa que o lóxico e que o final do dia todas as citas programadas para ese día se resolveran de algún xeito
Na parte de cadro de mandos poderiase incluir que se xerase un informe a partir dos gráficos mostrados. Esportando o gráfico e os datos asociados a él

