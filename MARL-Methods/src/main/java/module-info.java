open module marlkit.methods {
	requires java.logging;
	requires transitive marlkit.base;
    

    exports learningstructure;
    exports modelofotheragents;
    exports modelofotheragents.learning;
    exports rewardmodels;
    exports communicationimplementations;
    exports centralizedtraining;
}