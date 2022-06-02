---------------------------------------------------------------------------------------------------
-- Create sources:
---------------------------------------------------------------------------------------------------
-- stream of hotels stay data:
CREATE STREAM expedia_ext_stream (
  	id BIGINT,
  	user_id BIGINT,
  	srch_ci VARCHAR,
  	srch_co VARCHAR,
  	hotel_id BIGINT,
  	stay INT
	) WITH (  
	  	kafka_topic='expedia_ext',
	 	value_format='json',
	  	partitions=3
 	);

-- total amount of distinct hotels by stay:
CREATE TABLE total_distinct_hotels AS
	SELECT stay, 
	       COUNT_DISTINCT(hotel_id) AS distinct_hotels_amount
	FROM expedia_ext_stream 
	WINDOW TUMBLING (SIZE 1 MINUTES)
	GROUP BY stay;

-- total amount of hotels by stay:
CREATE TABLE total_hotels AS
	SELECT stay,
	       COUNT(*) AS hotels_amount
	FROM expedia_ext_stream 
	WINDOW TUMBLING (SIZE 1 MINUTES)
	GROUP BY stay;

