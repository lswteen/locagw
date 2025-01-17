# locagw
loca gateway inout bound



# 멀티모듈
```text
mvn archetype:generate \
    -DgroupId=com.lottecard.loca \
    -DartifactId=gateway \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false

mvn archetype:generate \
    -DgroupId=com.lottecard.loca \
    -DartifactId=gateway-domain \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false

mvn archetype:generate \
    -DgroupId=com.lottecard.loca \
    -DartifactId=gateway-util \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false   
        
```