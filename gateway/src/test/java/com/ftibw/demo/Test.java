package com.ftibw.demo;

import org.reactivestreams.Subscription;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author : Ftibw
 * @date : 2019/6/26 10:43
 */
public class Test {
    public static void main1(String[] args) {
        Instant now = Instant.now();
        long l = now.plus(-8, ChronoUnit.HOURS).toEpochMilli();
        System.out.println("比现在早" + (now.toEpochMilli() - l) / 8 / 60 / 60 / 1000 + "*8小时");

        //2
        System.out.println("=================================");
        Flux<Integer> ints = Flux.range(1, 3).doOnNext(System.out::println);
        Disposable subscribe = ints.subscribe();
        ints.subscribe(System.out::println);

        //3
        System.out.println("=================================");
        ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });
        ints.subscribe(System.out::println, error -> System.err.println("Error: " + error));

        //4
        System.out.println("=================================");
        ints = Flux.range(1, 4);
        ints.subscribe(System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"));

        //5
//        System.out.println("=================================");
//        ints = Flux.range(1, 4);
//        ints.subscribe(System.out::println,
//                error -> System.err.println("Error " + error),
//                () -> System.out.println("Done"),
//                sub -> sub.request(10));

        //6
//        System.err.println("=================================");
//        ints = Flux.range(1, 4);
//        ints.subscribe(System.err::println,
//                error -> System.err.println("Error " + error),
//                () -> {
//                    System.err.println("Done");
//                },
//                s -> {
//                    s.request(10);
//                });
//
//        ints.subscribe(new BaseSubscriber<Integer>() {
//
//            public void hookOnSubscribe(Subscription subscription) {
//                System.out.println("Subscribed");
//                request(1);
//            }
//
//            public void hookOnNext(Integer value) {
//                System.out.println(value);
//                request(1);
//            }
//        });

        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });

    }

    public static void main(String[] args) throws InterruptedException {
//        final Mono<String> mono = Mono.just("hello ");
//
//        Thread t = new Thread(() -> mono
//                .map(msg -> msg + "thread ")
//                .subscribe(v ->
//                        System.out.println(v + Thread.currentThread().getName())
//                )
//        );
//        t.start();
//        t.join();

        //使用当前线程
//        Scheduler scheduler = Schedulers.immediate();

        //elastic用于兼容遗留的阻塞式代码
//        Scheduler scheduler3 = Schedulers.elastic();

        //single和parallel中不能使用阻塞式的方法,否则IllegalStateException
        //例如:
        // block(),
        // blockFirst(),
        // blockLast() ( as well as iterating over toIterable() or toStream() )
//        Scheduler scheduler0 = Schedulers.single();
//        Scheduler scheduler1 = Schedulers.newSingle("task-0");
//        Scheduler scheduler2 = Schedulers.parallel();
//
//        Flux<String> f = Flux.just(1, 2, 0)
//                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
//                .onErrorReturn("Divided by zero");
//        f.subscribe(System.err::println);

//        Flux<String> flux =
//                Flux.interval(Duration.ofMillis(250))
//                        .map(input -> {
//                            if (input < 3) return "tick " + input;
//                            throw new RuntimeException("boom");
//                        })
//                        .onErrorReturn("Uh oh");
//
//        flux.subscribe(System.out::println);
//        Thread.sleep(2100);

//        Flux.interval(Duration.ofMillis(250))
//                .map(input -> {
//                    if (input < 3) return "tick " + input;
//                    throw new RuntimeException("boom");
//                })
//                .retry(1)
//                .elapsed()
//                .subscribe(System.out::println, System.err::println);
//
//        Thread.sleep(2100);

        Flux.<String>error(new IllegalArgumentException())
                .doOnError(System.out::println)
                .retryWhen(companion -> companion.take(3))
                .subscribe(System.out::println, System.err::println);

        Flux<String> flux =
                Flux.<String>error(new IllegalArgumentException())
                        .retryWhen(companion -> companion
                                .zipWith(Flux.range(1, 4),
                                        (error, index) -> {
                                            if (index < 4) return index;
                                            else throw Exceptions.propagate(error);
                                        })
                        );

//        GatewayFilter g = (exchange, chain) -> {
//            //If you want to build a "pre" filter you need to manipulate the
//            //request before calling chain.filter
//            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
//
//            //use builder to manipulate the request
//            return chain.filter(exchange.mutate().request(request).build());
//        };

    }

}
