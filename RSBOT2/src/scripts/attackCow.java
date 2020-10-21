package scripts;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;
@Script.Manifest(name="Cow Killer", description="Kills Cows", properties = "author=BigD; topic=999; client=4;")
public class attackCow extends PollingScript<ClientContext>{
    final static int cows[] = {2790, 2791, 2793, 2792};
    final static int giants[] = {2103, 2102, 2099, 2098, 2101, 2100};

    final static int food = 379;
    final static int cowhide = 1739;

    @Override
    public void start(){

        ctx.combat.autoRetaliate(true);
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
                System.out.println("hello");
                attack();
            }
        }
    }

    public boolean needsHeal(){
        return ctx.combat.health() < 20;
    }

    public boolean shouldAttack(){
        return !ctx.players.local().healthBarVisible();
    }

    public boolean hasFood(){
        return ctx.inventory.select().id(food).count() > 0;
    }

    public void attack(){
        //constant loop
        //System.out.println("Running");
        final Npc goblinToAttack = ctx.npcs.select().id(giants).select(new Filter<Npc>(){

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

    public void getHide(){
        final GroundItem pickHide = ctx.groundItems.select().id(cowhide).nearest().poll();
        pickHide.interact("Pickup");

    }
    public void heal(){
        //poll takes the top result
        //peak looks at top result
        Item foodtoEat = ctx.inventory.select().id(food).poll();
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
