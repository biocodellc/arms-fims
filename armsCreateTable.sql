Drop if table exists `armsExpeditions`;
CREATE TABLE `armsExpeditions` (
	  `expeditionId` int(11) unsigned NOT NULL,
	  `principalInvestigator` varchar(125) NOT NULL DEFAULT '',
	  `contactName` varchar(125) NOT NULL DEFAULT '',
	  `fundingSource` varchar(1000) NOT NULL DEFAULT '',
	  `envisionedDuration` int(4) NOT NULL,
	  `geographicScope` varchar(1000) NOT NULL DEFAULT '',
	  `goals` text NOT NULL,
	  `leadOrganization` varchar(255) NOT NULL DEFAULT '',
	  `contactEmail` varchar(255) NOT NULL DEFAULT '',
	  PRIMARY KEY (`expeditionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

Drop if table exists `deployments`;
CREATE TABLE `deployments` (
	  `deploymentId` varchar(255) NOT NULL,
	  `actualDeploymentDate` datetime NOT NULL,
	  `actualDeploymentTimeOfDay` datetime DEFAULT NULL,
	  `actualRecoveryDate` datetime NOT NULL,
	  `actualRecoveryTimeOfDay` datetime DEFAULT NULL,
	  `armsModel` varchar(255) NOT NULL,
	  `attachmentMethod` varchar(255) DEFAULT NULL,
	  `continentOcean` varchar(18) NOT NULL DEFAULT '',
	  `country` varchar(50) NOT NULL DEFAULT '',
	  `county` varchar(50) DEFAULT NULL,
	  `dataEntryPersonInCharge` varchar(125) DEFAULT NULL,
	  `deploymentPersonInCharge` varchar(125) DEFAULT NULL,
	  `depthInMeters` int(6) NOT NULL,
	  `depthOfBottomMeters` int(6) DEFAULT NULL,
	  `durationToProcessing` int(3) DEFAULT NULL,
	  `errorRadius` int(4) DEFAULT NULL,
	  `habitat` varchar(255) DEFAULT NULL,
	  `horizontalDatum` varchar(40) DEFAULT NULL,
	  `intendedDeploymentDate` datetime NOT NULL,
	  `intendedRecoveryDate` datetime NOT NULL,
	  `intendedSoakTimeInYears` int(3) NOT NULL,
	  `island` varchar(100) DEFAULT NULL,
	  `islandGroup` varchar(100) DEFAULT NULL,
	  `decimalLatitude` float NOT NULL,
	  `locality` varchar(100) DEFAULT NULL,
	  `decimalLongitude` float NOT NULL,
	  `newOrReused` varchar(6) NOT NULL DEFAULT '',
	  `notes` text,
	  `numLayers` int(3) NOT NULL,
	  `numReplicatesInSet` int(5) DEFAULT NULL,
	  `photoUrl` varchar(2083) DEFAULT '',
	  `processingPersonInCharge` varchar(125) DEFAULT NULL,
	  `recoveryPersonInCharge` varchar(125) DEFAULT NULL,
	  `region` varchar(100) DEFAULT NULL,
	  `removalProtocol` varchar(255) DEFAULT NULL,
	  `stateProvince` varchar(100) DEFAULT NULL,
	  `stationId` varchar(255) NOT NULL,
	  `hasScrubbieLayer` varchar(3) NOT NULL DEFAULT '',
	  `substrateType` varchar(4) DEFAULT NULL,
	  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	  `expeditionId` int(11) unsigned NOT NULL,
	  `laminates` varchar(3) NOT NULL DEFAULT '',
	  `weightsAttached` varchar(3) NOT NULL DEFAULT '',
	  `intentToBarcode` varchar(9) NOT NULL DEFAULT '',
	  `intentToCollectOtherDatatypes` varchar(9) NOT NULL DEFAULT '',
	  `intentToMetabarcode` varchar(9) NOT NULL DEFAULT '',
	  `intentToPhotographPlates` varchar(9) NOT NULL DEFAULT '',
	  `intentToPhotographSpecimens` varchar(9) NOT NULL DEFAULT '',
	  PRIMARY KEY (`id`),
	  KEY `FKk94yxos6l5qr88ur2eqxq1e2m` (`expeditionId`),
	  CONSTRAINT `FKk94yxos6l5qr88ur2eqxq1e2m` FOREIGN KEY (`expeditionId`) REFERENCES `armsExpeditions` (`expeditionId`),
	  CONSTRAINT `deployments_ibfk_1` FOREIGN KEY (`expeditionId`) REFERENCES `armsExpeditions` (`expeditionId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
