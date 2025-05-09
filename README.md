# Focare Sound

## Instalação do Virtual Audio Cable

Instalar o vb-audio virtual cable

link para baixar o driver:

`https://download.vb-audio.com/Download_CABLE/VBCABLE_Driver_Pack45.zip`

link do site:

`https://vb-audio.com/Cable/`

Após instalação clique no ícone de som do windows e os virtuais drives serão exibidos como nas imagens abaixo:

![alt text](https://github.com/akumosstl/virtualAudioCableJava/blob/main/Capturar.PNG)

![alt text](https://github.com/akumosstl/virtualAudioCableJava/blob/main/Capturar2.PNG)

## Inicializar a aplicação

Na pasta "out" deste repositório tem o arquivo .jar, entre no prompt de comandos (cmd) e digite o comando abaixo para 
inicializar o .jar:

`
java -jar virtualAudioCableJava.jar
`
ATENÇÂO: no mesmo diretório no qual está salvo o arquivo .jar você deve criar um arquivo igual ao arquivo "focare.properties"
deste repositório. Nele você deve configurar o caminho e o nome do arquivo de som que deseja utilizar. Lembrando
que este arquivo deve conter a extensão ".wav". Veja abaixo o exemplo do conteúdo do arquivo "focare.properties":

`
focare.path=C:\\Users\\merc_\\projetos\\Soundboard_test\\Ring09.wav
focare.speakers.name=Driver de som primário
focare.cable.name=CABLE Input (VB-Audio Virtual Cable
focare.mic.name=CABLE Output (VB-Audio Virtual
`

## Testando

Para realizar o teste abra dois clientes http. No meu caso abri o prompt (cmd) e deixe preparado o comando/post para 
realizar o "stop" da música. Veja comando abaixo:

`
powershell -command "Invoke-WebRequest -UseBasicParsing -Method POST -Uri http://localhost:8000/stop"
`

Depois abri o "postman" e deixei preparado o comando/post para realizar o "play" da música. Veja a uri abaixo e utilize
em seu client http:

`
http://127.0.0.1:8000/play
`
Agora execute o comando play, no meu caso, utilizando o http client "postman".

Ao realizar está chamada post/http a música será inicializada.

Para realizar o "stop" ou a parada da música execute o comando que deixou preparado no "cmd".

