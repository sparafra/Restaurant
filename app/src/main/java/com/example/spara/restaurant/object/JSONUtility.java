package com.example.spara.restaurant.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JSONUtility {

    public JSONUtility(){}

    public static String downloadJSON(final String urlWebService) {

                try {
                    URL url = new URL(urlWebService);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
    }

    public static List<User> fillUsers(String json)
    {
        List<User> list = new ArrayList<>();
        try {

            JSONArray jsonArray = new JSONArray(json);
            for(int k=0; k< jsonArray.length(); k++) {
                JSONObject obj = jsonArray.getJSONObject(k);
                User U = new User();
                U.setNumeroTelefono(obj.getString("NumeroTelefono"));
                U.setMail(obj.getString("Mail"));
                U.setPassword(obj.getString("Password"));
                U.setNome(obj.getString("Nome"));
                U.setCognome(obj.getString("Cognome"));
                U.setIndirizzo(obj.getString("Indirizzo"));
                U.setAmministratore(obj.getBoolean("Amministratore"));
                U.setConfermato(obj.getBoolean("Confermato"));
                U.setIdLocale(obj.getLong("idLocale"));

                list.add(U);
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            return list;
        }
    }

    public static User fillUser(String json) {

        try {
            User U = new User();

            JSONObject obj = new JSONObject(json);

            U.setNumeroTelefono(obj.getString("NumeroTelefono"));
            U.setMail(obj.getString("Mail"));
            U.setPassword(obj.getString("Password"));
            U.setNome(obj.getString("Nome"));
            U.setCognome(obj.getString("Cognome"));
            U.setIndirizzo(obj.getString("Indirizzo"));
            U.setAmministratore(obj.getBoolean("Amministratore"));
            U.setConfermato(obj.getBoolean("Confermato"));
            U.setIdLocale(obj.getLong("idLocale"));
            U.setDisabilitato(obj.getBoolean("Disabilitato"));

            return U;
        }catch (Exception e){e.printStackTrace(); return null;}
    }

    public static ArrayList<Product> fillProductsList(String json)
    {
        try {
            System.out.println(json);
            ArrayList<Product> listProducts = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            String[][] stocks = new String[jsonArray.length()][2];
            StringBuilder Ingredienti;
            //int nProdotti = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                boolean presente = false;
                JSONObject obj = jsonArray.getJSONObject(i);
                Product P = new Product();
                Ingredienti = new StringBuilder();
                /*
                for (int k = 0; k < stocks.length && !presente; k++) {
                    if (obj.getString("Name").equals(stocks[k][0])) {
                        presente = true;
                    }
                }

                 */
                if (!presente) {
                    P.setId(Integer.parseInt(obj.getString("id")));
                    P.setPrezzo(Float.parseFloat(obj.getString("Price")));
                    P.setTipo(obj.getString("Type"));
                    P.setNome(obj.getString("Name"));
                    P.setImageURL(obj.getString("ImageURL"));
                    P.setQuantity(obj.getInt("Quantity"));
                    P.setIdLocale(obj.getLong("idLocal"));

                    //stocks[nProdotti][0] = obj.getString("Name");
                    String prezzo = obj.getString("Price");

                    int nIngredienti = 0;
                    List<Ingredient> listIngredients = new ArrayList<>();
                    List<ReviewProduct> listReview = new ArrayList<>();

                    JSONArray jsonArrayIngredientsOfProduct = obj.getJSONArray("Ingredients");

                    for (int k = 0; k < jsonArrayIngredientsOfProduct.length(); k++) {
                        JSONObject tmpobj = jsonArrayIngredientsOfProduct.getJSONObject(k);
                        Ingredient I = new Ingredient();
                        //if (tmpobj.getString("Name").equals(stocks[nProdotti][0])) {
                        if (nIngredienti == 0) {
                            Ingredienti.append(tmpobj.getString("Name"));
                        } else {
                            Ingredienti.append(", " + tmpobj.getString("Name"));
                        }
                        I.setId(Integer.parseInt(tmpobj.getString("id")));
                        I.setNome(tmpobj.getString("Name"));
                        I.setPrezzo(Float.parseFloat(tmpobj.getString("Price")));
                        listIngredients.add(I);
                        nIngredienti++;
                        //}
                    }
                    JSONArray jsonArrayReviewOfProduct = obj.getJSONArray("Reviews");

                    for (int k = 0; k < jsonArrayReviewOfProduct.length(); k++) {
                        JSONObject tmpobj = jsonArrayReviewOfProduct.getJSONObject(k);
                        ReviewProduct RP = new ReviewProduct();

                        RP.setVoto(Integer.parseInt(tmpobj.getString("Voto")));
                        RP.setNumeroTelefono(tmpobj.getString("NumeroTelefono"));
                        Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tmpobj.getString("DataOra"));
                        RP.setDataOra(date1);
                        RP.setIdProduct(tmpobj.getLong("idProdotto"));
                        listReview.add(RP);
                        nIngredienti++;
                        //}
                    }
                    if (nIngredienti == 1 && Ingredienti.toString().equals("null")) {
                        Ingredienti.delete(0, Ingredienti.length());
                        Ingredienti.append("€" + prezzo);
                    } else {
                        Ingredienti.append(" €" + prezzo);
                    }
                    //stocks[nProdotti][1] = Ingredienti.toString().trim();
                    //nProdotti++;
                    P.setListIngredienti(listIngredients);
                    P.setListReview(listReview);
                    listProducts.add(P);
                }
            }
            return listProducts;

        }catch (Exception e){e.printStackTrace(); return null;}
    }

    public static Order fillOrder(String json) {

        try {
            Order O = new Order();
            //JSONArray jsonArray = new JSONArray(json);
            JSONObject obj = new JSONObject(json);

            O.setId(obj.getInt("idOrdine"));
            O.setStato(obj.getString("Stato"));
            if (obj.getBoolean("Asporto") == false) {
                O.setAsporto(false);
            } else {
                O.setAsporto(true);
            }
            O.setNumeroTelefono(obj.getString("NumeroTelefono"));
            O.setCosto(Float.parseFloat(obj.getString("Costo")));

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
            Date d1 = null;
            try {
                d1 = sdf3.parse(obj.getString("DataOra"));
            } catch (Exception e) {
                d1 = sdf4.parse(obj.getString("DataOra"));
            }

            O.setDateTime(d1);
            O.setCosto(Float.valueOf(obj.getString("Costo")));
            JSONArray products = obj.getJSONArray("Products");

            List<Product> listProductsOrder = new ArrayList<>();
            for (int k = 0; k < products.length(); k++) {
                JSONObject product = products.getJSONObject(k);

                System.out.println(products.toString());
                Product P = new Product();
                P.setId(product.getInt("idProdotto"));
                P.setPrezzo(Float.parseFloat(product.getString("Price")));
                P.setImageURL(product.getString("ImageURL"));
                P.setTipo(product.getString("Type"));
                P.setNome(product.getString("Name"));
                P.setQuantity(product.getInt("Quantity"));

                JSONArray ingredients = product.getJSONArray("Ingredients");
                List<Ingredient> listIngredient = new ArrayList<>();
                for (int j = 0; j < ingredients.length(); j++) {
                    JSONObject ingredient = ingredients.getJSONObject(j);

                    Ingredient I = new Ingredient();
                    I.setId(Integer.parseInt(ingredient.getString("idIngredient")));
                    I.setNome(ingredient.getString("Name"));
                    I.setPrezzo(Float.parseFloat(ingredient.getString("Price")));
                    listIngredient.add(I);

                }
                P.setListIngredienti(listIngredient);
                listProductsOrder.add(P);
            }

            O.setListProducts(listProductsOrder);
            return O;

        }catch (Exception e){e.printStackTrace(); return null;}
    }

    public static ArrayList<Order> fillOrderList(String json) {
        try {
            ArrayList<Order> OrderList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            System.out.println(jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                System.out.println(obj.toString());

                Order O = new Order();
                O.setId(obj.getInt("idOrdine"));
                O.setStato(obj.getString("Stato"));
                if (obj.getBoolean("Asporto") == false) {
                    O.setAsporto(false);
                } else {
                    O.setAsporto(true);
                }
                O.setNumeroTelefono(obj.getString("NumeroTelefono"));
                O.setCosto(Float.parseFloat(obj.getString("Costo")));

                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALIAN);
                SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
                Date d1 = null;
                try {
                    d1 = sdf3.parse(obj.getString("DataOra"));
                } catch (Exception e) {
                    d1 = sdf4.parse(obj.getString("DataOra"));
                }
                O.setDateTime(d1);
                System.out.println(O.getDateTime());
                System.out.println(obj.getString("DataOra"));
                JSONArray products = obj.getJSONArray("Products");
                System.out.println(products.toString());

                List<Product> listProductsOrder = new ArrayList<>();
                for (int k = 0; k < products.length(); k++) {
                    JSONObject product = products.getJSONObject(k);

                    System.out.println(products.toString());
                    Product P = new Product();
                    P.setId(product.getInt("idProdotto"));
                    P.setPrezzo(Float.parseFloat(product.getString("Price")));
                    P.setImageURL(product.getString("ImageURL"));
                    P.setTipo(product.getString("Type"));
                    P.setNome(product.getString("Name"));
                    P.setQuantity(product.getInt("Quantity"));

                    JSONArray ingredients = product.getJSONArray("Ingredients");
                    List<Ingredient> listIngredient = new ArrayList<>();
                    for (int j = 0; j < ingredients.length(); j++) {
                        JSONObject ingredient = ingredients.getJSONObject(j);

                        Ingredient I = new Ingredient();
                        I.setId(Integer.parseInt(ingredient.getString("idIngredient")));
                        I.setNome(ingredient.getString("Name"));
                        I.setPrezzo(Float.parseFloat(ingredient.getString("Price")));
                        listIngredient.add(I);

                    }
                    P.setListIngredienti(listIngredient);
                    listProductsOrder.add(P);
                }

                O.setListProducts(listProductsOrder);
                OrderList.add(O);

            }
            return OrderList;
        }catch (Exception e){e.printStackTrace(); return null;}
    }

    public static ArrayList<Ingredient> fillIngredientList(String json) {
        try {
            ArrayList<Ingredient> listIngredients = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Ingredient I = new Ingredient();
                I.setId(obj.getInt("idIngrediente"));
                I.setNome(obj.getString("Nome"));
                I.setPrezzo(Float.parseFloat(obj.getString("Costo")));
                listIngredients.add(I);

            }
            return listIngredients;

        }catch (Exception e){e.printStackTrace(); return null;}
    }

    public static ArrayList<Notice> fillNoticeList(String json)throws JSONException {

        ArrayList<Notice> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            Notice N = new Notice();
            N.setIdAvviso(obj.getInt("idAvviso"));
            N.setCreatoDa(obj.getString("CreatoDa"));
            N.setIdLocale(obj.getLong("idLocale"));
            N.setMessaggio(obj.getString("Messaggio"));
            N.setRicevutoDa(obj.getString("RicevutoDa"));
            N.setTipo(obj.getString("Tipo"));
            N.setTitolo(obj.getString("Titolo"));
            N.setStato(obj.getBoolean("Stato"));

            list.add(N);
        }
        return list;

    }
}
