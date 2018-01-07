package $organization$.$name$

import java.util.Properties

import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.{Consumed, StreamsBuilder, StreamsConfig}
import com.lightbend.kafka.scala.streams.ImplicitConversions._
import com.lightbend.kafka.scala.streams.KStreamS
import org.apache.kafka.streams.kstream.{Produced, Serialized}

object Hello extends App {

  val stringSerde = Serdes.String()
  val longSerde = Serdes.Long().asInstanceOf[Serde[Long]]

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p
  }
  val builder: StreamsBuilder = new StreamsBuilder()
  val textLines: KStreamS[String, String] = builder.stream("TextLinesTopic", Consumed.`with`(stringSerde, stringSerde))

  textLines
    .flatMapValues( line => line.split("\\\\W+").toIterable )
    .map( (_, w) => (w, 1l))
    .groupByKey(Serialized.`with`(stringSerde, longSerde))
    .count()
    .toStream
    .to("wordsCountTopic", Produced.`with`(stringSerde, longSerde))


  val streams: KafkaStreams = new KafkaStreams(builder.build, config)
  streams.start


}


