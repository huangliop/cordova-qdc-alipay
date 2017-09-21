//
//  CDVAlipay.m
//  X5
//
//  Created by 007slm on 12/8/14.
//
//

#import "CDVAlipay.h"
#import <CommonCrypto/CommonDigest.h>
#import <AlipaySDK/AlipaySDK.h>

@implementation CDVAlipay
-(void)handleOpenURL:(NSNotification *)notification{
    NSURL* url = [notification object];
    //跳转支付宝钱包进行支付，需要将支付宝钱包的支付结果回传给SDK
    if (url!=nil && [url.host isEqualToString:@"safepay"]) {
        [[AlipaySDK defaultService]
         processOrderWithPaymentResult:url
         standbyCallback:^(NSDictionary *resultDic) {
             NSLog(@"result = %@", resultDic);
             
             NSString *jsonString = [self jsonStringWithDictionary:resultDic];
             
             [self successWithCallbackID:self.currentCallbackId withMessage:jsonString];
         }];
    }
}


- (void)payment:(CDVInvokedUrlCommand*)command{
    
    self.currentCallbackId = command.callbackId;

    [self.commandDelegate runInBackground:^{
        // check arguments
         NSDictionary *params = [command.arguments objectAtIndex:0];
         if (!params)
         {
             [self failWithCallbackID:self.currentCallbackId withMessage:@"参数格式错误"];
             return ;
         }
        
         NSString *payInfo = nil;
         NSString *sign = nil;
        
         // check the params
         if (![params objectForKey:@"pay_info"])
         {
             [self failWithCallbackID:self.currentCallbackId withMessage:@"pay_info参数错误"];
             return ;
         }
         payInfo = [params objectForKey:@"pay_info"];

        //应用注册scheme,在AlixPayDemo-Info.plist定义URL types
        CDVViewController *viewController = (CDVViewController *)self.viewController;
        self.alipayScheme = [viewController.settings objectForKey:@"alipay"];
        
       
       [[AlipaySDK defaultService] payOrder:payInfo fromScheme:self.alipayScheme callback:^(NSDictionary *resultDic) {
           NSLog(@"reslut = %@", resultDic);

           NSString *jsonString = [self jsonStringWithDictionary:resultDic];

           [self successWithCallbackID:self.currentCallbackId withMessage:jsonString];
       }];
    }];
}


- (NSString *) jsonStringWithDictionary:(NSDictionary *) dictionary {
    
    NSString *jsonString = nil;
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:NSJSONWritingPrettyPrinted error:&error];
    if (error) {
        NSLog(@"Got an error: %@", error);
        jsonString = nil;
    } else {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    
    return jsonString;
}

- (void)successWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)failWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}
@end
