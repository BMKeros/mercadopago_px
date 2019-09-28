import 'dart:async';

import 'package:flutter/services.dart';

class MercadoPagoPx {
  String _publicKey;

  MercadoPagoPx(this._publicKey);

  static const MethodChannel _channel = const MethodChannel('mercadopagopx');

  Future<dynamic> startPayment(String preferenceId) async {
    return await _channel.invokeMethod('method#startPayment',
        {'public_key': _publicKey, 'preference_id': preferenceId});
  }
}
