open module marlkit.methods {
	requires java.logging;
	requires transitive marlkit.base;
    

	exports agentmodule;
	exports algorithm;
	exports centralizedtraining;
	exports communicationimplementation;
    exports learningstructure;
    exports modelofotheragents;
    exports modelofotheragents.factory; 
    exports rewardmodelimplementation;
}