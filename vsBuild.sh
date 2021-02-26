cd de.variantsync.core
mvn clean verify
eLevel = $?

if [eLevel -eq 0] 
then
   #Building library was successful
   cd ..
   mvn clean verify
   return $?
else
   #Building library was not successful
   return eLevel
fi   
