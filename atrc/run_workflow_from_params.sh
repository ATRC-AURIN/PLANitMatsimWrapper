function parse_yaml { 
  local s='[[:space:]]*' w='[a-zA-Z0-9_]*' fs=$(echo @|tr @ '\034')
  sed -ne "s|^\($s\):|\1|" -e "s|^\($s\)\($w\)$s:$s[\"']\(.*\)[\"']$s\$|\1$fs\2$fs\3|p" -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p"  $1 | awk -F$fs '{indent = length($1)/2; vname[indent] = $2; for (i in vname) {if (i > indent) {delete vname[i]}} if (length($3) > 0) {vn=""; for (i=0; i<indent; i++) {vn=(vn)(vname[i])("_")} printf("%s%s=\"%s\"\n", vn, $2, $3);}}'
}
eval $(parse_yaml /atrc_data/parameters.yaml)

VERSION=0.0.1a1
MODES=$inputs_MODES_value
CRS=$inputs_CRS_value
NETWORK=$inputs_NETWORK_path
NETWORK_CRS=$inputs_NETWORK_CRS_value
PLANS=$inputs_PLANS_path
PLANS_CRS=$inputs_PLANS_CRS_value
ACTIVITY_CONFIG=$inputs_ACTIVITY_CONFIG_path
LINK_STATS=$inputs_LINK_STATS_value
ITERATIONS_MAX=$inputs_ITERATIONS_MAX_value
OUTPUT=$inputs_OUTPUT_path
# Probably want to add some other test cases / other parameters.yamls to fill these out


PARAMS=""
[ ! -z "$MODES" ] && PARAMS="${PARAMS} --modes ${MODES}"
[ ! -z "$CRS" ] && PARAMS="${PARAMS} --crs ${CRS}"
[ ! -z "$NETWORK" ] && PARAMS="${PARAMS} --network ${NETWORK}"
[ ! -z "$NETWORK_CRS" ] && PARAMS="${PARAMS} --network_crs ${NETWORK_CRS}"
[ ! -z "$PLANS" ] && PARAMS="${PARAMS} --plans ${PLANS}"
[ ! -z "$PLANS_CRS" ] && PARAMS="${PARAMS} --plans_crs ${PLANS_CRS}"
[ ! -z "$ACTIVITY_CONFIG" ] && PARAMS="${PARAMS} --activity_config ${ACTIVITY_CONFIG}"
[ ! -z "$LINK_STATS" ] && PARAMS="${PARAMS} --link_stats ${LINK_STATS}"
[ ! -z "$ITERATIONS_MAX" ] && PARAMS="${PARAMS} --iterations_max ${ITERATIONS_MAX}"
[ ! -z "$OUTPUT" ] && PARAMS="${PARAMS} --output ${OUTPUT}"

# run a simulation, e.g. --type simulation, rather than creating a (custom) MATSim config file
CALL_STR="java -jar /app/jar/planit-aurin-matsim-${VERSION}.jar --type simulation ${PARAMS}"
echo $CALL_STR
eval $CALL_STR