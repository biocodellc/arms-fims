#############################
# Worksheet Level
#############################
Rename worksheet "Sheet1" to "Samples"

#############################
# Mandatory Columns in our specification but i couldn't find on the sheets:
#############################
armsModel
new/reused
hasScrubbieLayer
continentOcean
weightsAttached
stationID

#############################
# Fields on your sheet that i didn't know how to map, please provide a proper name
#############################
SITE ID -> 
Unit Label -> 
Year -> 
DEPLOYMENT ZONE -> 
SITE DETAILS -> 
MATALA MESH -> 

#############################
# Fields that should be deleted
#############################
DEPTH (ft) -> DELETE (we had this in meters in specification--- super confusing to also have feet)

#############################
# Field I suggest Renaming
#############################
ARMS IDs -> deploymentID 
COUNTRY -> country
REGION -> region
LOCALITY NAME -> locality
DEPLOYMENT DATE -> actualDeploymentDate
RECOVERY DATE -> actualRecoveryDate
INTENDED RECOVERY DATE -> intendedRecoveryDate
SOAK TIME Months -> intendedSoakTimeInYears
NUMBER OF replicate ARMS -> numReplicatesInSet
LATITUDE -> decimalLatitude
LONGITUDE -> decimalLongitude
Depth (m) -> depthInMeters
SUBSTRATE TYPE -> substrateType
NOTES -> notes
LAMINATES -> laminates
Layers number -> numLayers
??Plate Photos -> intentToPhotographPlates
??Specimen photos -> intentToPhotographSpecimens
Barcoding data -> intentToBarcode
Metabarcoding data -> intentToMetabarcode
Other data types -> intentToCollectOtherDatatypes

#############################
# Fields that should not be on spreadsheet (these are part of project)
#############################
LEAD ORGANISATION -> [THIS SHOULD NOT BE PART OF SPREADSHEET, PART OF PROJECT] 
LEAD PI -> [THIS SHOULD NOT BE PART OF SPREADSHEET, PART OF PROJECT]
PI EMAIL -> [THIS SHOULD NOT BE PART OF SPREADSHEET, PART OF PROJECT] 
Secondary contact person -> [THIS SHOULD NOT BE PART OF SPREADSHEET, PART OF PROJECT] 
PROJECT ID -> [THIS SHOULD NOT BE PART OF SPREADSHEET, PART OF PROJECT]
