package br.edu.ifba.saj.ads.pweb;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;



@QuarkusMain
public class Main {
    
    @JsonSerialize
    public record Produto(Integer id, String nome, String categoria, String marca, Double preco) {}


    public static void main(String[] args) {
        Quarkus.run(args);
    }

    @Startup
    public void startup() throws Exception {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        router.get("/busca").handler(this::buscarProdutos);

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }

    private void buscarProdutos(RoutingContext context) {
        Optional<String> categoria = Optional.ofNullable(context.request().getParam("categoria"));
        Optional<String> marca = Optional.ofNullable(context.request().getParam("marca"));

        // Lógica para buscar produtos no banco de dados
        // (Normalmente, isso envolveria consultar um banco de dados real)

        JsonArray jsonreturn = new JsonArray();
        
        List<Produto> produtos =  List.of(
                new Produto(1, "Smartphone Samsung Galaxy S20","Smartphone", "Samsung", 3500.00), 
                new Produto(2, "TV Samsung 50 polegadas","TV", "Samsung", 2500.00)
            );
        produtos.stream()
                    .filter(p -> categoria.isPresent() ?  p.categoria().equals(categoria.get()) : true)
                    .filter(p -> marca.isPresent() ? p.marca().equals(marca.get()) : true)
                    .forEach(p -> jsonreturn.add(new JsonObject(Json.encode(p))));

        context.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonreturn.encode());
    }
}

