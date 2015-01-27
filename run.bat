javac -classpath svgSalamander.jar *.java
@IF NOT ERRORLEVEL 1 java -classpath .;svgSalamander.jar SVGtoPolygon
@PAUSE
