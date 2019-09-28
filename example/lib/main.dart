import 'package:flutter/material.dart';
import 'package:mercadopago_px/mercadopago_px.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final MercadoPagoPx pm = MercadoPagoPx("");

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('MercadoPago - Android Payment Experience'),
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.payment),
          onPressed: () {
            pm.startPayment("");
          },
        ),
      ),
    );
  }
}
