#import "NaturalForms.h"
#import <Cordova/CDV.h>

@implementation NaturalForms

- (void)launchNaturalForms:(CDVInvokedUrlCommand*)command {
    
    CDVPluginResult* pluginResult = nil;
    
    NSString* strDocument = [command.arguments objectAtIndex:0];
    NSString* strValues = [command.arguments objectAtIndex:1];

    NSString *url = [NSString stringWithFormat:@"nfapi://x-callback-url/addDocument?documents=%@&values=%@&x-source=JobNimbus&x-error=jobnimbus://x-callback-url/1.0/addDocumentFailed", strDocument, strValues];

    NSLog(@"%@", url);

    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"nfapi://"]]) {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:(true)];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsBool:(false)];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end