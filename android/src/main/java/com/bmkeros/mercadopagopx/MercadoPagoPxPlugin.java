package com.bmkeros.mercadopagopx;

import android.content.Intent;

import com.google.gson.Gson;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.app.Activity.RESULT_CANCELED;

public class MercadoPagoPxPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
    private static int REQUEST_CODE_MERCADOPAGO_PX = 666;
    private final Registrar mRegistrar;
    private Result mResult;

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "mercadopagopx");

        final MercadoPagoPxPlugin plugin = new MercadoPagoPxPlugin(registrar);

        channel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
    }

    private MercadoPagoPxPlugin(Registrar registrar) {
        this.mRegistrar = registrar;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("method#startPayment")) {
            this.mResult = result;
            this.startPayment((String) call.argument("public_key"), (String) call.argument("preference_id"));
        } else {
            result.notImplemented();
        }
    }

    public void startPayment(String publicKey, String preferenceId) {
        new MercadoPagoCheckout.Builder(publicKey, preferenceId)
                .build()
                .startPayment(mRegistrar.activity(), REQUEST_CODE_MERCADOPAGO_PX);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MERCADOPAGO_PX) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {

                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);

                this.mResult.success(paymentToMap(payment));

                return true;

            } else if (resultCode == RESULT_CANCELED) {

                if (data != null
                        && data.getExtras() != null
                        && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError =
                            (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);

                    this.mResult.error(String.valueOf(resultCode), String.valueOf(resultCode), errorToMap(mercadoPagoError));

                    return false;
                } else {
                    this.mResult.error(String.valueOf(resultCode), String.valueOf(resultCode), null);
                    return false;
                }

            } else {
                this.mResult.error(String.valueOf(resultCode), String.valueOf(resultCode), null);
                return false;
            }
        }
        return false;
    }


    private Map<String, Object> paymentToMap(Payment payment) {
        final HashMap<String, Object> map = new HashMap<>();
        Gson gson = new Gson();

        map.put("id", payment.getId());
        map.put("status", payment.getPaymentStatus());
        map.put("statusDetail", payment.getPaymentStatusDetail());
        map.put("transactionAmount", payment.getTransactionAmount().doubleValue());
        map.put("transactionAmountRefunded", payment.getTransactionAmountRefunded().doubleValue());
        map.put("transactionDetails", gson.toJson(payment.getTransactionDetails()));
        map.put("paymentMethodId", payment.getPaymentMethodId());
        map.put("paymentTypeId", payment.getPaymentTypeId());
        map.put("description", payment.getDescription());
        map.put("card", cardToMap(payment.getCard()));
        map.put("payer", gson.toJson(payment.getPayer()));

        return map;
    }

    private Map<String, Object> cardToMap(Card card) {
        final HashMap<String, Object> map = new HashMap<>();

        map.put("cardHolder", card.getCardHolder());
        map.put("customerId", card.getCustomerId());
        map.put("dateCreated", card.getDateCreated());
        map.put("dateLastUpdated", card.getDateLastUpdated());
        map.put("expirationMonth", card.getExpirationMonth());
        map.put("expirationYear", card.getExpirationYear());
        map.put("firstSixDigits", card.getFirstSixDigits());
        map.put("id", card.getId());
        map.put("issuer", card.getIssuer());
        map.put("lastFourDigits", card.getLastFourDigits());
        map.put("paymentMethod", card.getPaymentMethod());
        map.put("securityCode", card.getSecurityCode());

        return map;
    }

    private Map<String, Object> errorToMap(MercadoPagoError mercadoPagoError) {
        final HashMap<String, Object> map = new HashMap<>();

        map.put("message", mercadoPagoError.getMessage());
        map.put("errorDetail", mercadoPagoError.getErrorDetail());
        map.put("requestOrigin", mercadoPagoError.getRequestOrigin());
        map.put("apiException", mercadoPagoError.getApiException());
        map.put("recoverable", mercadoPagoError.isRecoverable());

        return map;
    }


}
