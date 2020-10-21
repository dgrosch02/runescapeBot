package scripts;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import java.util.concurrent.Callable;

@Script.Manifest(name="Goblin Killer", description="Kills Goblins", properties = "author=BigD; topic=999; client=4;")

public class attackGoblin extends PollingScript<ClientContext> {

    final static int goblinId[] = { 3029, 3017, 3031, 3030, 3034, 3033, 3017 };
    final static int foodId = 379;


    @Override
    public void start(){
        System.out.println("Started");
    }

    @Override
    public void stop(){
        System.out.println("Stopped");
    }

    @Override
    public void poll() {
        if(hasFood()){
            if(needsHeal()) {
                heal();
            }
            else if(shouldAttack()){
                attack();
            }
        }
    }

    public boolean needsHeal(){
        return ctx.combat.health() < 6;
    }

    public boolean shouldAttack(){
        return !ctx.players.local().healthBarVisible();
    }

    public boolean hasFood(){
        return ctx.inventory.select().id(foodId).count() > 0;
    }

    public void attack(){
        //constant loop
        //System.out.println("Running");
        final Npc goblinToAttack = ctx.npcs.select().id(goblinId).select(new Filter<Npc>(){

            @Override
            public boolean accept(Npc npc) {
                return !npc.healthBarVisible();
            }
        }).nearest().poll();
        goblinToAttack.interact("Attack");



        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().healthBarVisible();

            }
        }, 150, 20);
    }


    public void heal(){
        //poll takes the top result
        //peak looks at top result
        Item foodtoEat = ctx.inventory.select().id(foodId).poll();
        //foodtoEat.interact("Eat", "Swordfish");
        foodtoEat.interact("Eat");

        final int startHealth = ctx.combat.health();
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final int currentHealth = ctx.combat.health();
                return currentHealth != startHealth;
            }
        }, 150, 20);


    }

}
