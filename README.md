# MiniBlas for Android devices
MiniBlas Android es una herramienta de debug que se conecta a la plataforma COSME, está desarrollado para el sistema operativo Android y escrito en lenguaje Java. La aplicación permite leer periódicamente los valores de las variables de las aplicaciones COSME, elegidas y  ordenadas en cestas. Cada una de estas cestas contiene un período de actualización y una lista personalizada de las variables en las que estamos interesados en visualizar.

Es posible crear diferentes cestas donde en cada una de ellas tener diferentes variables que visualicen partes de la aplicación, por ejemplo, podemos tener una cesta para  variables de entrada, otra para la visualización de sensores, etc.

## Manual de usuario MiniBlas Android
Una vez inicida la aplicación, automáticamente se conectará con el servicio Arcadio, en la interfaz principal de MiniBlas, podemos encontrar la lista de perfiles, donde el usuario puede seleccionar el que quiera para conectarse.   
 ![Config RT] (screenshots/1.jpg)

Podemos crear un nuevo perfil pulsando el botón con el símbolo de suma situado en la esquina inferior derecha, a continuación aparecerá un dialogo donde el usuario debe introducir los datos de un perfil como el nombre, el IP, el puerto y la contraseña. Una vez introducido los nombres, guardamos los cambios y volveremos a la pantalla anterior.

  ![Config RT] (screenshots/2.jpg)
  
Los perfiles se pueden ordenar libremente manteniendo pulsado en la parte izquierda de cada uno de los perfiles.
 
  ![Config RT] (screenshots/3.jpg)
  
Para modificar o eliminar los perfiles, mantenemos pulsado el perfil o perfiles, y pulsamos el botón del lapicero o de la papelera dependiendo si queremos editar o borrar el perfil seleccionado.
 
  ![Config RT] (screenshots/4.jpg)
  
Pulsamos en el perfil con el que deseamos conectarnos, a continuación se establecerá la conexión con el servidor y el icono rojo en la parte superior cambiara a verde. La interfaz mostrará todas las cestas asociadas al perfil seleccionado.
 
  ![Config RT] (screenshots/5.jpg)
  
Para crear una nueva cesta, pulsamos el botón situado en la esquina inferior derecha, a continuación introducimos el nombre de la cesta y el periodo de refresco en milisegundos con la que se refrescará en pantalla.
 
  ![Config RT] (screenshots/6.jpg)
  
Para editar las cestas, de la misma manera que antes, mantenemos pulsada la cesta seleccionada y pulsamos el botón del lapicero o de la papelera dependiendo si queremos editar o borrar la cesta seleccionada.
 
  ![Config RT] (screenshots/7.jpg)
  
Pulsamos la cesta que queramos visualizar, a continuación se enviarán al servidor las  variables que contenga la cesta. La interfaz mostrará todas las variables que se actualizarán con el periodo de refresco establecido en la cesta seleccionada.
     
  ![Config RT] (screenshots/8.jpg)  ![Config RT] (screenshots/9.jpg)  
  
Para añadir nuevas variables, pulsamos el botón situado en la parte inferior izquierda, el sistema nos muestra 3 tipos de Widgets de Visualización.
Visualizador: Mostrará únicamente el nombre de la variable y su valor. El usuario podrá editar el valor pulsando encima de la variable.
Barra de desplazamiento: El usuario debe elegir la variable y a continuación un valor máximo y minino y un valor de salto del deslizador. Cuando el usuario desplace la barra el nuevo valor se enviará al servidor.
Interruptor: El usuario establece un valor de encendido y un valor de apagado. Cuando pulse el botón se enviara al servidor el nuevo valor.
        
  ![Config RT] (screenshots/10.jpg)  ![Config RT] (screenshots/11.jpg)  
  
Al igual que en las demás interfaces, las variables se podrían ordenar libremente, y manteniendo pulsada en una o varias variables, podremos editar o eliminar las variables seleccionadas. 
Accediendo al menú de ajuste se pueden cambiar algunos ajustes como el puerto por defecto, el perfil por defecto de la autoconexión o modificar parámetros de la interfaz como el color.
       
  ![Config RT] (screenshots/12.jpg)  ![Config RT] (screenshots/13.jpg)   
  
En el botón ajustes, podemos activar o desactivar una pequeña terminal donde podremos ver posibles errores que recibamos del servidor COSME, u posibles excepciones generadas. La terminal se deslizara por la pantalla permitiendo colocara en pantalla completa, a media pantalla o esconderla.

   ![Config RT] (screenshots/14.jpg)  ![Config RT] (screenshots/15.jpg)  
