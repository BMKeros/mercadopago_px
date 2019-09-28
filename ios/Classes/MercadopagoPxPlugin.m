#import "MercadopagoPxPlugin.h"
#import <mercadopago_px/mercadopago_px-Swift.h>

@implementation MercadopagoPxPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMercadopagoPxPlugin registerWithRegistrar:registrar];
}
@end
